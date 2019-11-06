# -*- coding: utf-8 -*-
# @Time : 2019/11/4 12:43
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 获取行政区信息

from flask import request

from models import District, Street, Community, Garden
from utils import token_check
from . import generate_result


@token_check
def district(*args, **kwargs):
    districts = District.query.all()
    data = {'districts': [i.to_dict for i in districts]}
    return generate_result(0, '获取行政区数据成功', data)


@token_check
def street(*args, **kwargs):
    data = request.get_json()
    if 'districtId' in data:
        districtId = data['districtId']
        streets = Street.query.filter_by(districtId=districtId).all()
        result_list = []
        for i in streets:
            the_dict = i.to_dict
            the_dict.pop('districtId')
            result_list.append(the_dict)
        return generate_result(0, '获取街道数据成功', {'streets': result_list})


@token_check
def community(*args, **kwargs):
    data = request.get_json()
    if 'streetId' in data:
        streetId = data['streetId']
        communities = Community.query.filter_by(streetId=streetId).all()
        result_list = []
        for i in communities:
            the_dict = i.to_dict
            the_dict.pop('streetId')
            result_list.append(the_dict)
        return generate_result(0, '获取社区数据成功', {'communities': result_list})


@token_check
def garden(*args, **kwargs):
    data = request.get_json()
    if 'communityId' in data:
        communityId = data['communityId']
        gardens = Garden.query.filter_by(communityId=communityId).all()
        result_list = []
        for i in gardens:
            the_dict = i.to_dict
            the_dict.pop('communityId')
            result_list.append(the_dict)
        return generate_result(0, '获取小区数据成功', {'gardens': result_list})
