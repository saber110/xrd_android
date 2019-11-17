# -*- coding: utf-8 -*-
# @Time : 2019/11/11 23:25
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 数据获取模块

from io import BytesIO
from urllib.parse import quote

from flask import request, Blueprint, send_file
from openpyxl import Workbook
from sqlalchemy.exc import SQLAlchemyError

from . import generate_validator, generate_result
from .. import config
from ..models.garden import Garden
from ..models.garden_base_info import GardenBaseInfo
from ..models.map_data import MapData
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
    filename = quote('小区信息_数据导入表.xlsx')
    rv = send_file(stream,
                   mimetype='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
    rv.headers['Content-Disposition'] = f"attachment;filename*=utf-8''{filename}"
    return rv
