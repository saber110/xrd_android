# -*- coding: utf-8 -*-
# @Time : 2019/12/2 11:28
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 删除数据模块

import os

from flask import request, Blueprint
from sqlalchemy.exc import SQLAlchemyError

from . import generate_result, generate_validator
from .. import config
from ..models.base_model import db
from ..models.building_import_info import BuildingImportInfo
from ..models.building_info import BuildingInfo
from ..models.building_picture import BuildingPicture
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
    print(map_data.name)
    return generate_result(0, '删除地图数据成功')


@del_data_bp.route('/building_info', methods=['POST'])
@token_check
def building_info(*args, **kwargs):
    """
    删除地图数据接口
    """
    data = request.get_json()
    schema = {
        'buildingId': {'type': 'integer', 'min': 1},
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        BuildingImportInfo.query.filter_by(id=data['buildingId']).delete(synchronize_session=False)
        pictures = BuildingPicture.query.filter_by(buildingId=data['buildingId']).all()
        for picture in pictures:
            db.session.delete(picture)
        BuildingInfo.query.filter_by(id=data['buildingId']).delete(synchronize_session=False)
        db.session.commit()
    except SQLAlchemyError as e:
        print(str(e))
        db.session.rollback()
        return generate_result(2)
    # 删除文件
    for picture in pictures:
        origin_picture_path = os.path.join(config.UPLOADED_IMAGES_DEST, picture.originFilePath)
        compressed_picture_path = os.path.join(config.UPLOADED_IMAGES_DEST, picture.compressedFilePath)
        if os.path.exists(origin_picture_path):
            os.remove(origin_picture_path)
        if os.path.exists(compressed_picture_path):
            os.remove(compressed_picture_path)
    return generate_result(0, '删除楼栋数据成功')
