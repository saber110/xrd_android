# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:55
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 建筑图片种类表

from .base_model import db, BaseModel


class BuildingPictureKind(BaseModel):
    name = db.Column(db.String(64), primary_key=True, comment='建筑照片种类名称')
