# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:52
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 小区表

from .base_model import db, BaseModel


class Garden(BaseModel):
    """
    小区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='小区id')
    communityId = db.Column(db.Integer, db.ForeignKey('community.id'), nullable=False, comment='小区对应的社区id')
    streetId = db.Column(db.Integer, db.ForeignKey('street.id'), nullable=False, comment='小区对应的街道id')
    districtId = db.Column(db.Integer, db.ForeignKey('district.id'), nullable=False, comment='小区对应的行政区id')
    name = db.Column(db.String(85), nullable=False, comment='小区名称，最长为85个字符')

    __table_args__ = (
        db.UniqueConstraint('communityId', 'streetId', 'districtId', 'name', name='garden_index'),
    )
