# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:54
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 小区图片种类表

from .base_model import db, BaseModel


class GardenPictureKind(BaseModel):
    name = db.Column(db.String(64), primary_key=True, comment='小区照片种类名称')
