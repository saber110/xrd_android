# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:37
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 所有模型的基类

from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()


def to_dict(inst, cls):
    d = {}
    for c in cls.__table__.columns:
        v = getattr(inst, c.name)
        d[c.name] = v
    return d


class BaseModel(db.Model):
    __abstract__ = True

    def __init__(self, *args, **kwargs):
        """
        删除多余的参数
        :param args:
        :param kwargs:
        """
        for k in list(kwargs.keys()):
            if not hasattr(self, k):
                del kwargs[k]
        super().__init__(*args, **kwargs)

    def update(self, **kwargs):
        for k in list(kwargs.keys()):
            if hasattr(self, k):
                self.__setattr__(k, kwargs[k])

    @property
    def to_dict(self):
        return to_dict(self, self.__class__)
