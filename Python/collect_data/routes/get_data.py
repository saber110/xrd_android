# -*- coding: utf-8 -*-
# @Time : 2019/11/11 23:25
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 数据获取模块

from io import BytesIO

from flask import request, Blueprint
from openpyxl import Workbook
from sqlalchemy.exc import SQLAlchemyError

from . import generate_validator, generate_result, my_send_file
from .. import config
from ..models.building_info import BuildingInfo
from ..models.city import City
from ..models.community import Community
from ..models.district import District
from ..models.garden import Garden
from ..models.garden_base_info import GardenBaseInfo
from ..models.map_data import MapData
from ..models.street import Street
from ..utils import gcj02_to_bd09
from ..wraps import token_check, admin_required

get_data_bp = Blueprint('get_data', __name__, url_prefix=config.URL_Prefix + '/get_data')


@get_data_bp.route('/map', methods=['POST'])
@token_check
def map_data(*args, **kwargs):
    data = request.get_json()
    schema = {
        'gardenId': {'type': 'integer', 'min': 1},
        'mapId': {'type': 'integer', 'min': 0}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        all_map_data = MapData.query.filter_by(gardenId=data['gardenId']).all()
    except SQLAlchemyError:
        return generate_result(2, '查询数据失败')
    if data['mapId'] == 1:
        for item in all_map_data:
            item.longitude, item.latitude = gcj02_to_bd09(item.longitude, item.latitude)
    return generate_result(0, '获取地图数据成功', {'map_data': [i.to_dict for i in all_map_data]})


@get_data_bp.route('/garden_base_info', methods=['POST'])
@token_check
def garden_base_info(*args, **kwargs):
    """
    获取小区基本信息
    """
    data = request.get_json()
    schema = {
        'gardenId': {'type': 'integer', 'min': 1}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        garden = Garden.query.get(data['gardenId'])
        city = City.query.get(garden.cityId)
        district = District.query.get(garden.districtId)
        street = Street.query.get(garden.streetId)
        community = Community.query.get(garden.communityId)
        garden_info = GardenBaseInfo.query.get(data['gardenId'])
    except SQLAlchemyError:
        return generate_result(2, '获取数据失败')
    if garden is None:
        return generate_result(2, '小区不存在')
    result = [
        {
            'label': '市',
            'key': '',
            'required': False,
            'changed': False,
            'type': 'text',
            'value': city.name
        },
        {
            'label': '区县',
            'key': '',
            'required': False,
            'changed': False,
            'type': 'text',
            'value': district.name
        },
        {
            'label': '街道',
            'key': '',
            'required': False,
            'changed': False,
            'type': 'text',
            'value': street.name
        },
        {
            'label': '社区名称',
            'key': '',
            'required': False,
            'changed': False,
            'type': 'text',
            'value': community.name
        },
        {
            'label': '社区别名',
            'key': 'communityAlias',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区名称',
            'key': '',
            'required': False,
            'changed': False,
            'type': 'text',
            'value': garden.name
        },
        {
            'label': '小区别名',
            'key': 'gardenAlias',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区别名2',
            'key': 'communityAlias2',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区座落',
            'key': 'gardenLocation',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区东至',
            'key': 'gardenEastTo',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区西至',
            'key': 'gardenWestTo',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区北至',
            'key': 'gardenNorthTo',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区南至',
            'key': 'gardenSouthTo',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '区域位置',
            'key': 'regionalLocation',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ['城区中心', '城东区域', '城南区域', '城西区域', '城北区域', '近郊组团及乡镇', '远郊组团及乡镇'],
            'value': ''
        },
        {
            'label': '楼盘状态',
            'key': 'houseStatus',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["存量", "在建", "在售", "灭失"],
            'value': ''
        },
        {
            'label': '小区类型',
            'key': 'gardenKind',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["普通商品房", "老式住宅区", "经济适用房", "中档商品房", "高档商品房", "自建房", "零星住宅区", "农村拆迁房", "城市拆迁房", "人才专项房"],
            'value': ''
        },
        {
            "label": "建筑类型",
            "key": "buildingKind",
            "required": False,
            "changed": True,
            "type": "multiple",
            "option": ["住宅(电梯房)", "住宅(楼梯房)", "住宅(洋房)", "单身公寓(住宅)", "单身公寓(非住宅)", "办公写字楼", "别墅(独栋)", "别墅(联排)", "别墅(双拼)",
                       "叠墅", "自建民房", "其它类型"],
            "value": []
        },
        {
            'label': '房屋性质',
            'key': 'roomType',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["商品房", "房改房", "经济适用房", "集资房", "私房", "非商品房", "公租房", "安置房"],
            'value': ''
        },
        {
            'label': '建筑结构',
            'key': 'buildingStructure',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["钢混结构", "混合结构", "钢及钢混结构", "砖混结构", "砖木结构", "钢结构", "木结构", "简易结构"],
            'value': ''
        },
        {
            'label': '住宅幢数',
            'key': 'houseNumber',
            'required': True,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '非住宅幢数',
            'key': 'notHouseNumber',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '幢数描述',
            'key': 'description',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '建成年份',
            'key': 'buildYear',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '设定年份',
            'key': 'setYear',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '使用权',
            'key': 'right',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["出让", "划拨", "集体"],
            'value': ''
        },
        {
            'label': '土地等级',
            'key': 'right',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["一级", "二级", "三级", "四级", "五级", "六级", "七级", "八级", "九级", "十级", "十一级"],
            'value': ''
        },
        {
            'label': '询价记录',
            'key': 'askRecode',
            'required': True,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '是否封闭',
            'key': 'closed',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["是", "否"],
            'value': ''
        },
        {
            'label': '物管分类',
            'key': 'managementKind',
            'required': True,
            'changed': True,
            'type': 'radio',
            'option': ["专业物业管理", "社区保洁", "社区准物业", "无物业管理"],
            'value': ''
        },
        {
            'label': '价格初判',
            'key': 'beginPrice',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区概况表其它信息备注',
            'key': 'otherInfo',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '相邻小区',
            'key': 'neighborGarden',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '交通干道',
            'key': 'mainRoad',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            "label": "道路等级",
            "key": "roadGrade",
            "required": True,
            "changed": True,
            "type": "multiple",
            "option": ["主干道、快速路", "次干道", "街巷", "里弄", "特殊类型"],
            "value": []
        },
        {
            'label': '公交站名',
            'key': 'busStation',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '站点距离',
            'key': 'busStationDistance',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '普通公交',
            'key': 'baseBus',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '快速公交',
            'key': 'quickBus',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '线路条数',
            'key': 'busLines',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '地铁站',
            'key': 'subwayStation',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '地铁站距离',
            'key': 'subwayDistance',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '地铁线路条数',
            'key': 'subwayLines',
            'required': False,
            'changed': True,
            'type': 'number',
            'value': ''
        },
        {
            'label': '农贸市场',
            'key': 'farmerMarket',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '超市商场',
            'key': 'market',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '医疗设施',
            'key': 'hospital',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '金融设施',
            'key': 'bank',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '文体设施',
            'key': 'gym',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '行政机关',
            'key': 'organization',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '幼儿园',
            'key': 'kindergarten',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小学',
            'key': 'primary',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '中学',
            'key': 'middle',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '大学',
            'key': 'college',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '景点',
            'key': 'attractions',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '公园',
            'key': 'park',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '路牌号',
            'key': 'streetNumber',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '所处商圈',
            'key': 'businessArea',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '所处板块',
            'key': 'locationArea',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '周边利用',
            'key': 'aroundUse',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '河流山川',
            'key': 'riversAndMountains',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '噪音污染',
            'key': 'noisePollution',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '空气污染',
            'key': 'airPollution',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '不利设施',
            'key': 'adverseFacilities',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '其他污染',
            'key': 'otherPollution',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '繁华程度',
            'key': 'busyDegree',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '休闲设施',
            'key': 'relaxFacilities',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '运动设施',
            'key': 'sportFacilities',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '安保设施',
            'key': 'securityFacilities',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '建筑风格',
            'key': 'architecturalStyle',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区绿化',
            'key': 'gardenGreening',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '小区评价',
            'key': 'gardenEvaluation',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
        {
            'label': '物业公司',
            'key': 'propertyCompany',
            'required': False,
            'changed': True,
            'type': 'text',
            'value': ''
        },
    ]

    if garden_info is not None:
        garden_info = garden_info.to_dict
        for key in garden_info.keys():
            if garden_info[key] is None:
                garden_info[key] = ''
        for item in result:
            if item['key'] != '' and item['key'] in garden_info:
                item['value'] = garden_info[item['key']]
                if item['type'] == 'multiple':
                    item['value'] = item['value'].split(',')

    return generate_result(0, '获取数据成功', {'gardenInfoList': result})


@get_data_bp.route('/building_base_info', methods=['POST'])
@token_check
def building_base_info(*args, **kwargs):
    """
    获取楼栋基本信息
    """
    data = request.get_json()
    schema = {
        'buildingId': {'type': 'integer', 'min': 1}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        building_info = BuildingInfo.query.get(data['buildingId'])
    except SQLAlchemyError:
        return generate_result(2, '获取数据失败')
    result = building_info.to_dict
    for key in result.keys():
        if result[key] is None:
            result[key] = ''
    return generate_result(0, '获取数据成功', {'buildingInfo': result})


@get_data_bp.route('/garden_table', methods=['POST'])
@token_check
@admin_required
def garden_table(*args, **kwargs):
    """
    导出小区信息_数据导入表
    """
    data = request.get_json()
    schema = {
        'gardenId': {'type': 'integer', 'min': 1}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        garden = Garden.query.get(data['gardenId'])
        garden_info = GardenBaseInfo.query.get(data['gardenId'])
    except SQLAlchemyError:
        return generate_result(2, '导出数据失败')
    wb = Workbook()
    ws = wb.active
    ws.title = '表3 《小区信息_数据导入表》'
    table_head = ['小区名称', '小区别名', '路_牌_号', '界址街牌', '小区东至', '小区南至', '小区西至', '小区北至', '区域位置', '所处商圈', '所处板块', '楼盘状态',
                  '小区类型',
                  '建筑类型', '房屋性质', '建筑结构', '总_套_数', '建成年份', '设定年份', '施工单位', '容_积_率', '建筑密度', '开_发_商', '绿_化_率', '入_住_率',
                  '土地性质',
                  '使_用_权', '土地等级', '土地面积', '建筑规模', '住宅面积', '住宅幢数', '幢数描述', '物管公司', '销售代理', '售楼地址', '图_丘_号', '售楼电话',
                  '销售时间',
                  '地图来源', '定位坐标', '项目简介', '区外环境', '相邻小区', '交通干道', '道路等级', '公交站名', '站点距离', '普通公交', '快速公交', '线路条数',
                  '地铁站名',
                  '地铁距离', '地铁线路', '周边利用', '农贸市场', '超市商场', '医疗设施', '金融机构', '文体场馆', '行政机关', '幼_儿_园', '小学教育', '中学教育',
                  '大学教育',
                  '旅游景点', '河流山脉', '噪音污染', '空气污染', '不利设施', '其他污染', '其他影响', '繁华程度', '公园广场', '商圈距离', '基础设施', '内部道路',
                  '区内环境',
                  '小区会所', '休闲设施', '运动设施', '泊位数量', '泊位类型', '泊位配比', '商业配套', '是否封闭', '物管分类', '物业费用', '安保设施', '建筑风格',
                  '小区绿化',
                  '小区评价', '价格初判']
    table_map = {'小区别名': 'gardenAlias', '路_牌_号': 'streetNumber', '界址街牌': '', '小区东至': 'gardenEastTo',
                 '小区南至': 'gardenSouthTo', '小区西至': 'gardenWestTo', '小区北至': 'gardenNorthTo', '区域位置': 'regionalLocation',
                 '所处商圈': 'businessArea', '所处板块': 'locationArea', '楼盘状态': 'houseStatus', '小区类型': 'gardenKind',
                 '建筑类型': 'buildingKind', '房屋性质': 'roomType', '建筑结构': 'buildingStructure', '总_套_数': '',
                 '建成年份': 'buildYear', '设定年份': 'setYear', '施工单位': '', '容_积_率': '', '建筑密度': '', '开_发_商': '', '绿_化_率': '',
                 '入_住_率': '', '土地性质': 'landStatus', '使_用_权': 'right', '土地等级': 'landGrade', '土地面积': '', '建筑规模': '',
                 '住宅面积': '', '住宅幢数': 'houseNumber', '幢数描述': 'description', '物管公司': 'propertyCompany', '销售代理': '',
                 '售楼地址': '', '图_丘_号': '', '售楼电话': '', '销售时间': '', '地图来源': '', '定位坐标': '', '项目简介': '', '区外环境': '',
                 '相邻小区': 'neighborGarden', '交通干道': 'mainRoad', '道路等级': 'roadGrade', '公交站名': 'busStation',
                 '站点距离': 'busStationDistance', '普通公交': 'baseBus', '快速公交': 'quickBus', '线路条数': 'busLines',
                 '地铁站名': 'subwayStation', '地铁距离': 'subwayDistance', '地铁线路': 'subwayLines', '周边利用': 'aroundUse',
                 '农贸市场': 'farmerMarket', '超市商场': 'market', '医疗设施': 'hospital', '金融机构': 'bank', '文体场馆': 'gym',
                 '行政机关': 'organization', '幼_儿_园': 'kindergarten', '小学教育': 'primary', '中学教育': 'middle',
                 '大学教育': 'college', '旅游景点': 'attractions', '河流山脉': 'riversAndMountains', '噪音污染': 'noisePollution',
                 '空气污染': 'noisePollution', '不利设施': 'adverseFacilities', '其他污染': 'otherPollution', '其他影响': '',
                 '繁华程度': 'busyDegree', '公园广场': 'park', '商圈距离': '', '基础设施': '', '内部道路': '', '区内环境': '', '小区会所': '',
                 '休闲设施': 'relaxFacilities', '运动设施': 'sportFacilities', '泊位数量': '', '泊位类型': '', '泊位配比': '', '商业配套': '',
                 '是否封闭': 'closed', '物管分类': 'managementKind', '物业费用': '', '安保设施': 'securityFacilities',
                 '建筑风格': 'architecturalStyle', '小区绿化': 'gardenGreening', '小区评价': 'gardenEvaluation', '价格初判': ''}
    ws.append(table_head)
    garden_info = garden_info.to_dict
    for key in garden_info.keys():
        if garden_info[key] is None:
            garden_info[key] = ''
    table_data = [garden.name]
    table_head.pop(0)
    for key in table_head:
        data_base_key = table_map[key]
        if data_base_key == '':
            table_data.append('')
        else:
            table_data.append(garden_info[data_base_key])
    ws.append(table_data)
    stream = BytesIO()
    wb.save(stream)
    stream.seek(0)
    return my_send_file(stream, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '小区信息_数据导入表.xlsx')


@get_data_bp.route('/building_table', methods=['POST'])
@token_check
@admin_required
def building_table(*args, **kwargs):
    """
    导出楼幢信息_数据导入表
    """
    data = request.get_json()
    schema = {
        'buildingId': {'type': 'integer', 'min': 1}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        building_info = BuildingInfo.query.get(data['buildingId'])
    except SQLAlchemyError:
        return generate_result(2, '导出数据失败')
    wb = Workbook()
    ws = wb.active
    ws.title = '表4《楼幢信息_数据导入表》'
    table_head = ['楼幢名称', '楼幢别名', '楼幢东至', '楼幢西至', '楼幢南至', '楼幢北至', '物业分类', '单元名称', '室号名称', '单_元_数', '单_元_号', '楼_层_号',
                  '楼层差异', '住宅起始', '总_套_数', '主_朝_向', '部位说明', '建筑结构', '建成年份', '地上总层', '地下总层', '住房层高', '装修标准', '装修描述',
                  '装修时间', '户型分类', '房产性质', '图_丘_号', '辅房用途', '架_空_层', '地上车库', '地下车库', '建筑面积', '是否别墅', '一梯几户', '得_房_率',
                  '楼幢状态', '建筑风格', '设备设施', '外墙饰面', '消防设施', '电梯设施', '空调设施', '有无热水', '大堂入口', '顶楼情况', '顶楼露台', '顶楼阁楼',
                  '顶楼跃层', '屋面情况', '电梯大堂', '公共部位', '平面布局', '通风采光', '营_业_房', '使用状况', '幢外景观', '中心花园', '不利设施', '噪声污染',
                  '楼幢间距', '三临情况', '其他不利', '其他有利', '楼幢评价', '价格初判', '地图来源', '定位坐标']
    table_map = {'楼幢名称': 'buildingName', '楼幢别名': 'buildingAlias', '楼幢东至': '', '楼幢西至': '', '楼幢南至': '', '楼幢北至': '',
                 '物业分类': 'propertyKind', '单元名称': 'unitName', '室号名称': 'roomName', '单_元_数': 'numberOfUnit',
                 '单_元_号': 'unitNumber', '楼_层_号': 'floorNumber', '楼层差异': 'floorDifferent', '住宅起始': 'beginFloor',
                 '总_套_数': '', '主_朝_向': 'mainTowards', '部位说明': 'locationDescription', '建筑结构': 'buildingStructure',
                 '建成年份': 'completedYear', '地上总层': 'floorOverGround', '地下总层': 'floorUnderGround', '住房层高': '',
                 '装修标准': 'decorationStandard', '装修描述': '', '装修时间': 'decorationYear', '户型分类': '',
                 '房产性质': 'buildingProperty', '图_丘_号': '', '辅房用途': '', '架_空_层': '架_空_层', '地上车库': '地上车库', '地下车库': '',
                 '建筑面积': '',
                 '是否别墅': 'isVilla', '一梯几户': 'oneLiftNumber', '得_房_率': '', '楼幢状态': 'buildingStatus', '建筑风格': '',
                 '设备设施': '', '外墙饰面': '', '消防设施': '', '电梯设施': '', '空调设施': '', '有无热水': '', '大堂入口': '',
                 '顶楼情况': 'roofTopInfo', '顶楼露台': 'roofTopTerrace', '顶楼阁楼': 'roofTopAttic', '顶楼跃层': 'roofTopLayer',
                 '屋面情况': 'roofInfo', '电梯大堂': '', '公共部位': '', '平面布局': '', '通风采光': '', '营_业_房': '', '使用状况': '',
                 '幢外景观': '', '中心花园': '', '不利设施': '', '噪声污染': '', '楼幢间距': '', '三临情况': '', '其他不利': '', '其他有利': '',
                 '楼幢评价': '', '价格初判': '', '地图来源': '', '定位坐标': ''}
    ws.append(table_head)
    building_info = building_info.to_dict
    building_info['架_空_层'] = '_'
    building_info['地上车库'] = '_'
    #   添加1F情况说明key
    if building_info['firstFloorDescription'] == '架_空_层':
        building_info['架_空_层'] = '√'
    elif building_info['firstFloorDescription'] == '地上车库':
        building_info['地上车库'] = '√'
    for key in building_info.keys():
        if building_info[key] is None:
            building_info[key] = ''
    table_data = []
    for key in table_head:
        data_base_key = table_map[key]
        if data_base_key == '':
            table_data.append('')
        else:
            table_data.append(building_info[data_base_key])
    ws.append(table_data)
    stream = BytesIO()
    wb.save(stream)
    stream.seek(0)
    return my_send_file(stream, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '楼幢信息_数据导入表.xlsx')


@get_data_bp.route('/test', methods=['GET'])
def test(*args, **kwargs):
    test_list = [
        {
            "label": "文本输入测试",
            "key": "test1",
            "required": False,
            "changed": True,
            "type": "text",
            "value": ""
        },
        {
            "label": "二级列表测试",
            "type": "list",
            "length": 2
        }, {
            "label": "单选测试",
            "key": "test3",
            "required": True,
            "changed": True,
            "type": "radio",
            "option": ["test1", "test2"],
            "value": ""
        }, {
            "label": "多选测试",
            "key": "test4",
            "required": True,
            "changed": True,
            "type": "multiple",
            "option": ["test1", "test2", "test3"],
            "value": []
        },
        {
            "label": "文本测试",
            "key": "test5",
            "required": False,
            "changed": True,
            "type": "text",
            "value": "test5"
        }
    ]
    return generate_result(0, '测试数据', data={'formList': test_list})
