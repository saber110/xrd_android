# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:51
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 街道表

from .base_model import db, BaseModel


class Street(BaseModel):
    """
    街道表
    """
    id = db.Column(db.Integer, primary_key=True, comment='街道id')
    districtId = db.Column(db.Integer, db.ForeignKey('district.id'), nullable=False, comment='街道对应的行政区id')
    name = db.Column(db.String(85), nullable=False, comment='街道名称，最长为85个字符')
    __table_args__ = (
        db.UniqueConstraint('districtId', 'name', name='districtId_name'),
    )
