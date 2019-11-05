# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 定义数据库模型
import bcrypt
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from itsdangerous import BadTimeSignature, BadSignature
import time

from app import db, redis_client
import config


def to_dict(inst, cls):
    d = {}
    for c in cls.__table__.columns:
        v = getattr(inst, c.name)
        d[c.name] = v
    return d


class BaseModel(db.Model):
    __abstract__ = True

    @property
    def to_dict(self):
        return to_dict(self, self.__class__)


class User(BaseModel):
    """
    用户表
    """
    id = db.Column(db.Integer, primary_key=True, comment='用户id')
    iemi = db.Column(db.String(17), unique=True, nullable=False, comment='用户收集的IEMI号')
    realName = db.Column(db.String(32), nullable=False, comment='用户真实姓名，最长32个字符')
    phoneNumber = db.Column(db.String(11), unique=True, nullable=False, comment='用户手机号')
    password = db.Column(db.String(64), nullable=False, comment='用户密码')
    permission = db.Column(db.Integer, server_default='0', nullable=False, comment='用户权限')

    def set_password(self, password):
        self.password = bcrypt.hashpw(bytes(password, encoding='utf-8'), bcrypt.gensalt())

    def check_password(self, password):
        return bcrypt.checkpw(bytes(password, encoding='utf-8'), bytes(self.password, encoding='utf-8'))

    def generate_auth_token(self, expiration=config.EXPIRATION):
        s = Serializer(config.SECRET_KEY, expires_in=expiration)
        return s.dumps({'id': self.id, 'timestamp': time.time()})

    @staticmethod
    def verify_auth_token(token):
        s = Serializer(config.SECRET_KEY)
        try:
            data = s.loads(token)
        except BadTimeSignature:
            return None
        except BadSignature:
            return None
        max_timestamp = redis_client.get(data['id'])
        # 检查是否晚于token黑名单时间
        if max_timestamp is not None:
            if data['timestamp'] > max_timestamp:
                return None
        user = User.query.get(data['id'])
        return user


class District(BaseModel):
    """
    行政区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='行政区id')
    name = db.Column(db.String(85), nullable=False, comment='行政区名称，最长为85个字符（根据行政区命名规则长度限制制定）')


class Street(BaseModel):
    """
    街道表
    """
    id = db.Column(db.Integer, primary_key=True, comment='街道id')
    districtId = db.Column(db.Integer, db.ForeignKey('district.id'), nullable=False, comment='街道对应的行政区id')
    name = db.Column(db.String(85), nullable=False, comment='街道名称，最长为85个字符')


class Community(BaseModel):
    """
    社区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='社区id')
    streetId = db.Column(db.Integer, db.ForeignKey('street.id'), nullable=False, comment='社区对应的街道id')
    name = db.Column(db.String(85), nullable=False, comment='社区名称，最长为85个字符')


class Garden(BaseModel):
    """
    小区表
    """
    id = db.Column(db.Integer, primary_key=True, comment='小区id')
    communityId = db.Column(db.Integer, db.ForeignKey('community.id'), nullable=False, comment='小区对应的社区id')
    streetId = db.Column(db.Integer, db.ForeignKey('street.id'), nullable=False, comment='小区对应的街道id')
    districtId = db.Column(db.Integer, db.ForeignKey('district.id'), nullable=False, comment='小区对应的行政区id')
    name = db.Column(db.String(85), nullable=False, comment='小区名称，最长为85个字符')
