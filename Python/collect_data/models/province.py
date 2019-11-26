# -*- coding: utf-8 -*-
# @Time : 2019/11/22 23:33
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 省表

from .base_model import db, BaseModel


class Province(BaseModel):
    """
    省列表
    """
    id = db.Column(db.Integer, primary_key=True, comment='省id')
    name = db.Column(db.String(85), unique=True, nullable=False, comment='省名称，最长为85个字符（根据行政区命名规则长度限制制定）')
