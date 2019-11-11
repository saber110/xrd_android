# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:51
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 社区表

from .base_model import db, BaseModel


class Community(BaseModel):
    """
    社区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='社区id')
    streetId = db.Column(db.Integer, db.ForeignKey('street.id'), nullable=False, comment='社区对应的街道id')
    name = db.Column(db.String(85), nullable=False, comment='社区名称，最长为85个字符')
