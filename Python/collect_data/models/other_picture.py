# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:56
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 其他图片表

from .base_model import db, BaseModel


class OtherPicture(BaseModel):
    id = db.Column(db.Integer, primary_key=True, comment='其他照片id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='小区id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='上传照片的用户的id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='收集时间')
    syncTime = db.Column(db.DateTime, nullable=False, comment='同步时间')
    filePath = db.Column(db.Text, nullable=False, comment='文件储存路径')
