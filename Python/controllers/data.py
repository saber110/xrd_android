# -*- coding: utf-8 -*-
# @Time : 2019/11/4 21:41
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 收集小区数据功能实现

from flask import request
from sqlalchemy.exc import DBAPIError
from datetime import datetime

from . import generate_result, generate_validator
from utils import token_check
from models import *
from app import db, image_upload
from utils import bd09_to_gcj02


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


@token_check
def building(*args, **kwargs):
    """
    获取建筑种类数据
    :return:
    """
    buildings = Building.query.all()
    return generate_result(0, '获取建筑种类数据成功', {'buildingKinds': [i.to_dict for i in buildings]})


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


@token_check
def garden_picture_kind(*args, **kwargs):
    """
    获取小区图片类型
    :return:
    """
    garden_picture_kinds = GardenPictureKind.query.all()
    return generate_result(0, '获取小区图片种类成功', {'gardenPictureKinds': [i.to_dict['name'] for i in garden_picture_kinds]})


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
    pictures = GardenPicture.query.filter_by(gardenId=garden_id, pictureKind=picture_kind).all()
    try:
        garden = Garden.query.get(garden_id)
    except DBAPIError:
        return generate_result(2)
    number = f"{len(pictures) + 1:03d}"
    file_path = f'{garden.id}/origin/2_{garden.name}_{picture_kind}_{number}.'
    file_path = image_upload.save(image, name=file_path)
    # TODO 压缩并保存图片
    picture = GardenPicture(gardenId=garden_id, pictureKind=picture_kind, collectTime=collect_time,
                            filePath=file_path,
                            syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except DBAPIError:
        # TODO 对图片进行删除回滚
        return generate_result(2)
    return generate_result(0, '上传小区图片成功')


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
    file_path = f'{garden.id}/origin/4_{garden.name}_{number}.'
    file_path = image_upload.save(image, name=file_path)
    # TODO 压缩并保存图片
    picture = OtherPicture(gardenId=garden_id, collectTime=collect_time,
                           filePath=file_path, syncTime=datetime.now(), userId=user_id)
    try:
        db.session.add(picture)
        db.session.commit()
    except DBAPIError:
        # TODO 对图片进行删除回滚
        return generate_result(2)
    return generate_result(0, '上传小区其他图片成功')


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
