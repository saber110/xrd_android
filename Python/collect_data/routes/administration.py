# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:09
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 行政区数据获取

from flask import request, Blueprint

from . import generate_result
from .. import config
from ..models.community import Community
from ..models.district import District
from ..models.garden import Garden
from ..models.street import Street
from ..utils import token_check

administration_bp = Blueprint('administration', __name__, url_prefix=config.URL_Prefix + '/administration')


@administration_bp.route('/district', methods=['POST'])
@token_check
def district(*args, **kwargs):
    districts = District.query.all()
    data = {'districts': [i.to_dict for i in districts]}
    return generate_result(0, '获取行政区数据成功', data)


@administration_bp.route('/street', methods=['POST'])
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


@administration_bp.route('/community', methods=['POST'])
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


@administration_bp.route('/garden', methods=['POST'])
@token_check
def garden(*args, **kwargs):
    data = request.get_json()
    if 'communityId' in data:
        community_id = data['communityId']
        gardens = Garden.query.filter_by(communityId=community_id).all()
        result_list = []
        for i in gardens:
            the_dict = i.to_dict
            the_dict.pop('communityId')
            result_list.append(the_dict)
        return generate_result(0, '获取小区数据成功', {'gardens': result_list})
