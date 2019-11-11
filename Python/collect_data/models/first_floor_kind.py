# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:57
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 楼栋一楼的可能情况

from .base_model import db, BaseModel


class FirstFloorKind(BaseModel):
    """
    楼栋一楼的可能情况
    """
    kind = db.Column(db.String(64), primary_key=True, comment='一楼可能的情况最多64个字符')
