# -*- coding: utf-8 -*-
# @Time : 2019/12/2 11:28
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 删除数据模块

from flask import request, Blueprint
from sqlalchemy.exc import SQLAlchemyError

from . import generate_result, generate_validator
from .. import config
from ..models.base_model import db
from ..models.map_data import MapData
from ..wraps import token_check

del_data_bp = Blueprint('del_data', __name__, url_prefix=config.URL_Prefix + '/del_data')


@del_data_bp.route('/map', methods=['POST'])
@token_check
def map_data(*args, **kwargs):
    """
    删除地图数据接口
    """
    data = request.get_json()
    schema = {
        'id': {'type': 'integer', 'min': 1},
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        map_data = MapData.query.get(data['id'])
        if map_data is None:
            return generate_result(2, '地图数据不存在')
        db.session.delete(map_data)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        return generate_result(2)
    return generate_result(0, '删除地图数据成功')
