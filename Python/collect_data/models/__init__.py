# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:35
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : model 初始化的一些操作

__all__ = ['base_model', 'building', 'building_info', 'building_picture', 'building_picture_kind', 'city', 'community',
           'district', 'first_floor_kind', 'garden', 'garden_base_info', 'garden_picture', 'garden_picture_kind',
           'map_data', 'other_picture', 'province', 'street', 'user']

from flask import Flask
from flask_migrate import Migrate

from .base_model import db
from .user import redis_client


def init_app(app: Flask):
    db.init_app(app)
    redis_client.init_app(app)
    from .building import Building
    from .building_info import BuildingInfo
    from .building_picture import BuildingPicture
    from .building_picture_kind import BuildingPictureKind
    from .city import City
    from .community import Community
    from .district import District
    from .first_floor_kind import FirstFloorKind
    from .garden import Garden
    from .garden_base_info import GardenBaseInfo
    from .garden_picture import GardenPicture
    from .garden_picture_kind import GardenPictureKind
    from .map_data import MapData
    from .other_picture import OtherPicture
    from .province import Province
    from .street import Street
    from .user import User
    Migrate(app, db)
