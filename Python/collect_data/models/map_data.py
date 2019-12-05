# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 地图数据表

from .base_model import db, BaseModel


class MapData(BaseModel):
    """
    地图数据表
    """
    id = db.Column(db.Integer, primary_key=True, comment='地图数据id')
    kindId = db.Column(db.Integer, db.ForeignKey('building.id'), nullable=False, comment='建筑类别id')
    longitude = db.Column(db.Float(precision='10,6'), nullable=False, comment='坐标经度，统一使用GCJ-02标准存储')
    latitude = db.Column(db.Float(precision='10,6'), nullable=False, comment="坐标维度，统一使用GCJ-02标准存储")
    name = db.Column(db.String(64), nullable=False, comment='建筑名称，最长64个字符')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='建筑所属的小区')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='添加信息的用户id')
