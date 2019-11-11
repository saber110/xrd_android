# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:40
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 收集数据相关

import os
from datetime import datetime

from flask import request, Blueprint
from sqlalchemy.exc import DBAPIError

from . import generate_result, generate_validator, image_upload
from .. import config
from ..models.base_model import db
from ..models.building import Building
from ..models.building_info import BuildingInfo
from ..models.building_picture import BuildingPicture
from ..models.building_picture_kind import BuildingPictureKind
from ..models.first_floor_kind import FirstFloorKind
from ..models.garden import Garden
from ..models.garden_base_info import GardenBaseInfo
from ..models.garden_picture import GardenPicture
from ..models.garden_picture_kind import GardenPictureKind
from ..models.map_data import MapData
from ..models.other_picture import OtherPicture
from ..utils import bd09_to_gcj02, compress_image, token_check

data_bp = Blueprint('data', __name__, url_prefix=config.URL_Prefix + '/data')


@data_bp.route('/garden', methods=['POST'])
@token_check
def garden(*args, **kwargs):
    """
    添加新的小区
    :return:
    """
    data = request.get_json()
    schema = {
        'districtId': {'type': 'integer', 'min': 1},
        "streetId": {'type': 'integer', 'min': 1},
        "communityId": {'type': 'integer', 'min': 1},
        "gardenName": {'type': 'string', 'maxlength': 85}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['name'] = data.pop('gardenName')
    garden = Garden(**data)
    communityGardens = Garden.query.filter_by(communityId=data['communityId']).all()
    for existGarden in communityGardens:
        if garden.name == existGarden.name:
            return generate_result(2, '请勿添加重名的小区')
    try:
        db.session.add(garden)
        db.session.commit()
    except DBAPIError:
        return generate_result(2, '添加小区失败')
    return generate_result(0, '添加小区成功', {'gardenId': garden.id})


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
    map_data = MapData(**data)
    try:
        db.session.add(map_data)
        db.session.commit()
    except DBAPIError:
        return generate_result(2, '添加建筑失败')
    return generate_result(0, '添加建筑成功')


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
    pictures = GardenPicture.query.filter_by(gardenId=garden_id, pictureKind=picture_kind).all()
    try:
        garden = Garden.query.get(garden_id)
    except DBAPIError:
        return generate_result(2)
    number = f"{len(pictures) + 1:03d}"
    file_path = f'origin/{garden.id}/2_{garden.name}_{picture_kind}_{number}.'
    file_path = image_upload.save(image, name=file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, file_path)
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST,
                                   f'compressed/{garden.id}/2_{garden.name}_{picture_kind}_{number}.jpg')
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = GardenPicture(gardenId=garden_id, pictureKind=picture_kind, collectTime=collect_time,
                            filePath=file_path,
                            syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except DBAPIError:
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
        building_id = request.form['buildingId']
        garden_id = request.form['gardenId']
        collect_time = request.form['collectTime']
        image = request.files['image']
    except KeyError:
        return generate_result(1)
    collect_time = datetime.fromtimestamp(int(collect_time) / 1000.0)
    pictures = BuildingPicture.query.filter_by(buildingId=building_id).all()
    try:
        garden = Garden.query.get(garden_id)
        building = BuildingInfo.query.get(building_id)
    except DBAPIError:
        return generate_result(2)
    number = f"{len(pictures) + 1:03d}"
    file_path = f'origin/{garden_id}/{building_id}/3_{garden.name} {building.buildingName}_{picture_kind}_{number}.'
    file_path = image_upload.save(image, name=file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, file_path)
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST,
                                   f'compressed/{garden_id}/{building_id}/3_{garden.name} {building.buildingName}_{picture_kind}_{number}.jpg')
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = BuildingPicture(buildingId=building_id, pictureKind=picture_kind, collectTime=collect_time,
                              filePath=file_path,
                              syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except DBAPIError:
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
    pictures = OtherPicture.query.all()
    try:
        garden = Garden.query.get(garden_id)
    except DBAPIError:
        return generate_result(2)
    number = f"{len(pictures) + 1:03d}"
    file_path = f'origin/{garden.id}/4_{garden.name}_{number}.'
    file_path = image_upload.save(image, name=file_path)
    origin_path = os.path.join(config.UPLOADED_IMAGES_DEST, file_path)
    compressed_path = os.path.join(config.UPLOADED_IMAGES_DEST,
                                   f'compressed/{garden.id}/4_{garden.name}_{number}.jpg')
    compress_image(origin_path, compressed_path, config.COMPRESSED_SIZE)
    picture = OtherPicture(gardenId=garden_id, collectTime=collect_time,
                           filePath=file_path, syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except DBAPIError:
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

    try:
        base_info = GardenBaseInfo(**data)
        db.session.add(base_info)
        db.session.commit()
    except DBAPIError as e:
        print(str(e))
        return generate_result(2)
    return generate_result(0, '上传小区基本数据成功')


@data_bp.route('/first_floor_kind', methods=['POST'])
@token_check
def first_floor_kind(*args, **kwargs):
    """
    返回楼栋一楼可能存在的情况
    """
    return generate_result(0, '查询楼栋一楼情况成功', {'firstFloorKind': [i.to_dict['kind'] for i in FirstFloorKind.query.all()]})


@data_bp.route('/building_info', methods=['POST'])
@token_check
def building_info(user_id: int, *args, **kwargs):
    """
    楼栋调查表
    :param user_id: 用户id
    :return:
    """
    data = request.get_json()
    schema = {
        "gardenId": {'type': 'integer', 'min': 1},
        "collectTime": {'type': 'integer', 'min': 1},
        "buildingName": {'type': 'string'}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    data['userId'] = user_id
    data['collectTime'] = datetime.fromtimestamp(int(data['collectTime']) / 1000.0)

    try:
        info = BuildingInfo(**data)
        db.session.add(info)
        db.session.commit()
    except DBAPIError as e:
        print(str(e))
        return generate_result(2)
    return generate_result(0, '提交楼栋信息成功')
