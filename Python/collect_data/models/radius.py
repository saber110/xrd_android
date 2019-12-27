# -*- coding: utf-8 -*-
# @Time : 2019/12/26 22:18
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 半径表

from .base_model import db, BaseModel


class Radius(BaseModel):
    """
    省列表
    """
    key = db.Column(db.String(100), primary_key=True, comment='数据库中的key')
    name = db.Column(db.Text, comment='表格中的名称')
    radius = db.Column(db.Integer, comment='地图搜索半径，单位米')
