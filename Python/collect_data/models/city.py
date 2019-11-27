# -*- coding: utf-8 -*-
# @Time : 2019/11/22 23:29
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 城市列表

from .base_model import db, BaseModel


class City(BaseModel):
    """
    城市列表
    """
    id = db.Column(db.Integer, primary_key=True, comment='城市id')
    provinceId = db.Column(db.Integer, db.ForeignKey('province.id'), nullable=False, comment='城市对应的省id')
    name = db.Column(db.String(85), nullable=False, comment='城市名称，最长为85个字符（根据行政区命名规则长度限制制定）')
    __table_args__ = (
        db.UniqueConstraint('provinceId', 'name', name='provinceId_name'),
    )
