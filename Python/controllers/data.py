# -*- coding: utf-8 -*-
# @Time : 2019/11/4 21:41
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 收集小区数据功能实现

from flask import request
from sqlalchemy.exc import DBAPIError

from . import generate_result, generate_validator
from utils import token_check
from models import Garden
from app import db


@token_check
def garden(*args, **kwargs):
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
        del data['token']
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
