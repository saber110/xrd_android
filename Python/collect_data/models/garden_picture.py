# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:54
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 小区图片表

from .base_model import db, BaseModel


class GardenPicture(BaseModel):
    id = db.Column(db.Integer, primary_key=True, comment='小区图片id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='小区id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='上传照片的用户的id')
    pictureKind = db.Column(db.String(64), db.ForeignKey('garden_picture_kind.name'), nullable=False, comment='小区图片种类')
    collectTime = db.Column(db.DateTime, nullable=False, comment='收集时间')
    syncTime = db.Column(db.DateTime, nullable=False, comment='同步时间')
    originFilePath = db.Column(db.Text, nullable=False, comment='原始文件储存路径')
    compressedFilePath = db.Column(db.Text, nullable=False, comment='压缩文件储存路径')
