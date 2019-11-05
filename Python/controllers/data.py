# -*- coding: utf-8 -*-
# @Time : 2019/11/4 21:41
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 收集小区数据功能实现

from flask import request
from sqlalchemy.exc import DBAPIError

from . import generate_result, generate_validator
from utils import token_check
from models import Garden, Building, MapData
from app import db
from utils import bd09_to_gcj02


@token_check
def garden(*args, **kwargs):
    """
    添加新的小区
    :return:
    """
    if request.method == 'POST':
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
        return generate_result(0, '添加小区成功')


@token_check
def building(*args, **kwargs):
    """
    获取建筑种类数据
    :return:
    """
    if request.method == 'POST':
        buildings = Building.query.all()
        return generate_result(0, '获取建筑种类数据成功', {'buildingKinds': [i.to_dict for i in buildings]})


@token_check
def map(user_id: int, *args, **kwargs):
    """
    添加建筑标定信息
    :return:
    """
    if request.method == 'POST':
        data = request.get_json()
        schema = {
            "longitude": {'type': 'float', 'min': -180, 'max': 180},
            "latitude": {'type': 'float', 'min': -90, 'max': 90},
            "kindId": {'type': 'integer', 'min': 1},
            "name": {'type': 'string', 'maxlength': 64},
            "gardenId": {'type': 'integer', 'min': 1},
            "mapId": {'type': 'integer', 'min': 1, 'max': 3}
        }
        v = generate_validator(schema)
        if not v(data):
            return generate_result(1, data=v.errors)
        if data['mapId'] == 2:  # 如果是百度地图则进行转换
            data['longitude'], data['latitude'] = bd09_to_gcj02(data['longitude'], data['latitude'])
        data['userId'] = user_id
        map_data = MapData(**data)
        try:
            db.session.add(map_data)
            db.session.commit()
        except DBAPIError:
            return generate_result(2, '添加建筑失败')
        return generate_result(0, '添加建筑成功')
