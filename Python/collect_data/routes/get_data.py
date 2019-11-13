# -*- coding: utf-8 -*-
# @Time : 2019/11/11 23:25
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 数据获取模块

from flask import request, Blueprint

from . import generate_validator, generate_result
from .. import config
from ..models.map_data import MapData
from ..utils import gcj02_to_bd09
from ..wraps import token_check

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
    all_map_data = MapData.query.filter_by(gardenId=data['gardenId']).all()
    if data['mapId'] == 1:
        for item in all_map_data:
            item.longitude, item.latitude = gcj02_to_bd09(item.longitude, item.latitude)
    return generate_result(0, '获取地图数据成功', {'map_data': [i.to_dict for i in all_map_data]})
