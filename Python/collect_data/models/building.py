# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:52
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 建筑类别表

from .base_model import db, BaseModel


class Building(BaseModel):
    """
    建筑类别表
    """
    id = db.Column(db.Integer, primary_key=True, comment='建筑类别id')
    kindName = db.Column(db.String(64), unique=True, comment='类别名称，最长64个字符')
