# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:48
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc :
from .base_model import db, BaseModel


class District(BaseModel):
    """
    行政区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='行政区id')
    cityId = db.Column(db.Integer, db.ForeignKey('city.id'), nullable=False, comment='行政区对应的城市id')
    name = db.Column(db.String(85), nullable=False, comment='行政区名称，最长为85个字符（根据行政区命名规则长度限制制定）')

    __table_args__ = (
        db.UniqueConstraint('cityId', 'name', name='cityId_name'),
    )
