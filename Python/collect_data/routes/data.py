# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:40
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 收集数据相关

import os
from datetime import datetime

from flask import request, Blueprint
from sqlalchemy.exc import SQLAlchemyError

from . import generate_result, generate_validator, image_upload
from .administration import add_community
from .. import config
from ..models.base_model import db
from ..models.building import Building
from ..models.building_import_info import BuildingImportInfo
from ..models.building_info import BuildingInfo
from ..models.building_picture import BuildingPicture
from ..models.building_picture_kind import BuildingPictureKind
from ..models.first_floor_kind import FirstFloorKind
from ..models.garden import Garden
from ..models.garden_base_info import GardenBaseInfo
from ..models.garden_import_info import GardenImportInfo
from ..models.garden_picture import GardenPicture
from ..models.garden_picture_kind import GardenPictureKind
from ..models.map_data import MapData
from ..models.other_picture import OtherPicture
from ..utils import bd09_to_gcj02, compress_image, get_suffix
from ..wraps import token_check

data_bp = Blueprint('data', __name__, url_prefix=config.URL_Prefix + '/data')


@data_bp.route('/garden', methods=['POST'])
@token_check
def garden(user_id, *args, **kwargs):
    """
    添加新的小区
    :return:
    """
    data = request.get_json()
    data['userId'] = user_id
    schema = {
        "gardenName": {'type': 'string', 'maxlength': 85}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    is_virtual = False
    for item in ['provinceId', 'cityId', 'districtId', "streetId", "communityId"]:
        if item not in data:
            is_virtual = True
            break
    # 判断是否是虚拟社区
    if is_virtual:
        result = add_community('不清楚', '不清楚', '不清楚', '不清楚', '不清楚')
        data['provinceId'] = result['province'].id
        data['cityId'] = result['city'].id
        data['districtId'] = result['district'].id
        data['streetId'] = result['street'].id
        data['communityId'] = result['community'].id

    data['name'] = data.pop('gardenName')
    if 'gardenId' in data:
        garden = Garden.query.get(data['gardenId'])
        if garden is None:
            return generate_result(2, '小区不存在')
        garden.update(**data)
    else:
        garden = Garden(**data)
    try:
        db.session.add(garden)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2, '修改小区失败')
    return generate_result(0, '修改小区成功', {'gardenId': garden.id})


@data_bp.route('/init_building', methods=['POST'])
@token_check
def init_building(user_id: int, *args, **kwargs):
    """
    初始化楼幢信息
    """
    data = request.get_json()
    schema = {
        'gardenId': {'type': 'integer', 'min': 1},
        'number': {'type': 'integer', 'min': 1}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    already_building = BuildingInfo.query.filter_by(gardenId=data['gardenId']).all()
    building_ids = [i.id for i in already_building]
    add_number = data['number'] - len(already_building)
    insert_building = [BuildingInfo(collectTime=datetime.now(), gardenId=data['gardenId'], userId=user_id) for i in
                       range(add_number)]
    try:
        db.session.add_all(insert_building)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        return generate_result(2, '初始化楼幢失败')
    for info in insert_building:
        building_ids.append(info.id)
    try:
        for id in building_ids:
            import_info = BuildingImportInfo.query.get(id)
            if import_info is None:
                db.session.add(BuildingImportInfo(collectTime=datetime.now(), id=id, userId=user_id))
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        return generate_result(3, '初始化失败')
    return generate_result(0, '初始化楼幢成功', {'buildingIds': building_ids})


@data_bp.route('/building', methods=['POST'])
@token_check
def building(*args, **kwargs):
    """
    获取建筑种类数据
    :return:
    """
    buildings = Building.query.all()
    return generate_result(0, '获取建筑种类数据成功', {'buildingKinds': [i.to_dict for i in buildings]})


@data_bp.route('/map', methods=['POST'])
@token_check
def map_data(user_id: int, *args, **kwargs):
    """
    添加建筑标定信息
    :return:
    """
    data = request.get_json()
    schema = {
        "longitude": {'type': 'float', 'min': -180, 'max': 180},
        "latitude": {'type': 'float', 'min': -90, 'max': 90},
        "kindId": {'type': 'integer', 'min': 1},
        "name": {'type': 'string', 'maxlength': 64},
        "gardenId": {'type': 'integer', 'min': 1},
        "mapId": {'type': 'integer', 'min': 0, 'max': 2}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    if data['mapId'] == 1:  # 如果是百度地图则进行转换
        data['longitude'], data['latitude'] = bd09_to_gcj02(data['longitude'], data['latitude'])
    data['userId'] = user_id
    try:
        if 'id' in data:
            map_data = MapData.query.get(data['id'])
            if map_data is None:
                return generate_result(2, '地图数据不存在')
            map_data.update(**data)
        else:
            map_data = MapData(**data)
        db.session.add(map_data)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2, '添加建筑失败')
    return generate_result(0, '添加数据成功', {'mapDataId': map_data.id})


@data_bp.route('/garden_picture_kind', methods=['POST'])
@token_check
def garden_picture_kind(*args, **kwargs):
    """
    获取小区图片类型
    :return:
    """
    garden_picture_kinds = GardenPictureKind.query.all()
    return generate_result(0, '获取小区图片种类成功', {'gardenPictureKinds': [i.to_dict['name'] for i in garden_picture_kinds]})


@data_bp.route('/garden_picture', methods=['POST'])
@token_check
def garden_picture(user_id: int, *args, **kwargs):
    """
    上传小区图片
    :param user_id: 用户id
    :return:
    """
    try:
        garden_id = request.form['gardenId']
        picture_kind = request.form['pictureKind']
        collect_time = request.form['collectTime']
        image = request.files['image']
    except KeyError:
        return generate_result(1)
    collect_time = datetime.fromtimestamp(int(collect_time) / 1000.0)
    try:
        garden = Garden.query.get(garden_id)
        if garden is None:
            return generate_result(2, '小区不存在')
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2)
    origin_file_path = f'origin/{garden.id}/2_{garden.name}_{picture_kind}_{datetime.now().timestamp()}' + get_suffix(
        image.filename)
    origin_file_path = image_upload.save(image, name=origin_file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, origin_file_path)
    compressed_file_path = f'compressed/{garden.id}/2_{garden.name}_{picture_kind}_{datetime.now().timestamp()}.jpg'
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST, compressed_file_path)
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = GardenPicture(gardenId=garden_id, pictureKind=picture_kind, collectTime=collect_time,
                            originFilePath=origin_file_path, compressedFilePath=compressed_file_path,
                            syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        os.remove(origin_path)
        os.remove(compressed_path)
        # 对图片进行删除回滚
        return generate_result(2)
    return generate_result(0, '上传小区图片成功')


@data_bp.route('/building_picture_kind', methods=['POST'])
@token_check
def building_picture_kind(*args, **kwargs):
    """
    获取楼栋照片存在的种类
    :return:
    """
    return generate_result(0, '获取楼栋照片种类成功',
                           {'buildingPictureKinds': [i.to_dict['name'] for i in BuildingPictureKind.query.all()]})


@data_bp.route('/building_picture', methods=['POST'])
@token_check
def building_picture(user_id: int, *args, **kwargs):
    """
    上传楼栋照片
    :param user_id: 用户id
    :return:
    """
    try:
        picture_kind = request.form['pictureKind']
        building_name = request.form['buildingName']
        garden_id = request.form['gardenId']
        collect_time = request.form['collectTime']
        image = request.files['image']
    except KeyError:
        return generate_result(1)
    collect_time = datetime.fromtimestamp(int(collect_time) / 1000.0)
    try:
        garden = Garden.query.get(garden_id)
        if garden is None:
            return generate_result(2, '小区不存在')
        building = BuildingInfo.query.filter_by(gardenId=garden_id, buildingName=building_name).first()
        if building is None:
            building = BuildingInfo(collectTime=datetime.now(), gardenId=garden_id, userId=user_id,
                                    buildingName=building_name)
            db.session.add(building)
            db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2)
    origin_file_path = f'origin/{garden_id}/{building.id}/3_{garden.name} {building.buildingName}_{picture_kind}_{datetime.now().timestamp()}' + get_suffix(
        image.filename)
    origin_file_path = image_upload.save(image, name=origin_file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, origin_file_path)
    compressed_file_path = f'compressed/{garden_id}/{building.id}/3_{garden.name} {building.buildingName}_{picture_kind}_{datetime.now().timestamp()}.jpg'
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST, compressed_file_path)
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = BuildingPicture(buildingId=building.id, pictureKind=picture_kind, collectTime=collect_time,
                              originFilePath=origin_file_path, compressedFilePath=compressed_file_path,
                              syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        os.remove(origin_path)
        os.remove(compressed_path)
        # 对图片进行删除回滚
        return generate_result(2)
    return generate_result(0, '上传小区图片成功')


@data_bp.route('/other_picture', methods=['POST'])
@token_check
def other_picture(user_id: int, *args, **kwargs):
    """
    上传小区的其他图片
    :param user_id: 用户id
    :return:
    """
    try:
        garden_id = request.form['gardenId']
        collect_time = request.form['collectTime']
        image = request.files['image']
    except KeyError:
        return generate_result(1)
    collect_time = datetime.fromtimestamp(int(collect_time) / 1000.0)
    try:
        garden = Garden.query.get(garden_id)
        if garden is None:
            return generate_result(2, '小区不存在')
    except SQLAlchemyError:
        return generate_result(2)
    origin_file_path = f'origin/{garden.id}/4_{garden.name}_{datetime.now().timestamp()}' + get_suffix(image.filename)
    origin_file_path = image_upload.save(image, name=origin_file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, origin_file_path)
    compressed_file_path = f'compressed/{garden.id}/4_{garden.name}_{datetime.now().timestamp()}.jpg'
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST, compressed_file_path)
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = OtherPicture(gardenId=garden_id, collectTime=collect_time,
                           originFilePath=origin_file_path, compressedFilePath=compressed_file_path,
                           syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        os.remove(origin_path)
        os.remove(compressed_path)
        # 对图片进行删除回滚
        return generate_result(2)
    return generate_result(0, '上传小区其他图片成功')


@data_bp.route('/garden_base_info', methods=['POST'])
@token_check
def garden_base_info(user_id: int, *args, **kwargs):
    """
    提交小区概况表
    :param user_id: 用户id
    :return:
    """
    data = request.get_json()
    schema = {
        'id': {'type': 'integer'},
        'collectTime': {'type': 'integer'},
        'gardenLocation': {'type': 'string'},
        'gardenEastTo': {'type': 'string'},
        'gardenWestTo': {'type': 'string'},
        'gardenNorthTo': {'type': 'string'},
        'gardenSouthTo': {'type': 'string'},
        'regionalLocation': {'type': 'string'},
        'houseStatus': {'type': 'string'},
        'gardenKind': {'type': 'string'},
        'roomType': {'type': 'string'},
        'buildingStructure': {'type': 'string'},
        'houseNumber': {'type': 'integer'},
        'description': {'type': 'string'},
        'buildYear': {'type': 'integer'},
        'setYear': {'type': 'integer'},
        'landStatus': {'type': 'string'},
        'right': {'type': 'string'},
        'landGrade': {'type': 'string'},
        'askRecode': {'type': 'string'},
        'closed': {'type': 'string'},
        'managementKind': {'type': 'string'},
        'roadGrade': {'type': 'string'},
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['userId'] = user_id
    data['collectTime'] = datetime.fromtimestamp(int(data['collectTime']) / 1000.0)
    for key in list(data.keys()):
        if key not in schema and str(data[key]).strip() == '':
            del data[key]
    try:
        base_info = GardenBaseInfo.query.get(data['id'])
        if base_info is None:
            base_info = GardenBaseInfo(**data)  # 新建
        else:
            base_info.update(**data)  # 更新信息
        db.session.add(base_info)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        return generate_result(2)
    return generate_result(0, '上传小区基本数据成功')


@data_bp.route('/first_floor_kind', methods=['POST'])
@token_check
def first_floor_kind(*args, **kwargs):
    """
    返回楼栋一楼可能存在的情况
    """
    return generate_result(0, '查询楼栋一楼情况成功', {'firstFloorKind': [i.to_dict['kind'] for i in FirstFloorKind.query.all()]})


@data_bp.route('/building_base_info', methods=['POST'])
@token_check
def building_base_info(user_id: int, *args, **kwargs):
    """
    楼栋调查表
    :param user_id: 用户id
    :return:
    """
    data = request.get_json()
    schema = {
        "gardenId": {'type': 'integer', 'min': 1},
        "collectTime": {'type': 'integer', 'min': 1},
        "buildingName": {'type': 'string'},
        "buildingKind": {'type': 'string'}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['userId'] = user_id
    data['collectTime'] = datetime.fromtimestamp(int(data['collectTime']) / 1000.0)
    for key in list(data.keys()):
        if key not in schema and str(data[key]).strip() == '':
            del data[key]
    try:
        if 'id' in data:
            info = BuildingInfo.query.get(data['id'])
            if info is None:
                return generate_result(2, '楼幢数据不存在')
            info.update(**data)
        else:
            info = BuildingInfo(**data)
        db.session.add(info)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2)
    return generate_result(0, '提交楼栋信息成功')


@data_bp.route('/garden_import_info', methods=['POST'])
@token_check
def garden_import_info(user_id: int, *args, **kwargs):
    """
    添加或修改小区导入信息表
    :param user_id:  用id
    :return:
    """
    data = request.get_json()
    schema = {
        'id': {'type': 'integer'},
        'collectTime': {'type': 'integer'}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['userId'] = user_id
    data['collectTime'] = datetime.fromtimestamp(int(data['collectTime']) / 1000.0)
    for key in list(data.keys()):
        if key not in schema and str(data[key]).strip() == '':
            del data[key]
    try:
        import_info = GardenImportInfo.query.get(data['id'])
        if import_info is None:
            import_info = GardenImportInfo(**data)  # 新建信息
        else:
            import_info.update(**data)  # 更新信息

        db.session.add(import_info)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2)
    return generate_result(0, '上传小区导入数据成功')


@data_bp.route('/building_import_info', methods=['POST'])
@token_check
def building_import_info(user_id: int, *args, **kwargs):
    """
    添加或修改楼幢导入信息表
    :param user_id:  用id
    :return:
    """
    data = request.get_json()
    schema = {
        'id': {'type': 'integer'},
        'collectTime': {'type': 'integer'}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['userId'] = user_id
    data['collectTime'] = datetime.fromtimestamp(int(data['collectTime']) / 1000.0)
    for key in list(data.keys()):
        if key not in schema and str(data[key]).strip() == '':
            del data[key]
    try:
        import_info = BuildingImportInfo.query.get(data['id'])
        if import_info is None:
            import_info = BuildingImportInfo(**data)  # 新建信息
        else:
            import_info.update(**data)  # 更新信息
        db.session.add(import_info)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2)
    return generate_result(0, '上传楼幢导入数据成功')
