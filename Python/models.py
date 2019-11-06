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
        return data['id']


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


class Building(BaseModel):
    """
    建筑类别表
    """
    id = db.Column(db.Integer, primary_key=True, comment='建筑类别id')
    kindName = db.Column(db.String(64), unique=True, comment='类别名称，最长64个字符')


class MapData(BaseModel):
    """
    地图数据表
    """
    id = db.Column(db.Integer, primary_key=True, comment='地图数据id')
    kindId = db.Column(db.Integer, db.ForeignKey('building.id'), nullable=False, comment='建筑类别id')
    longitude = db.Column(db.Float(precision='10,6'), nullable=False, comment='坐标经度，统一使用GCJ-02标准存储')
    latitude = db.Column(db.Float(precision='10,6'), nullable=False, comment="坐标维度，统一使用GCJ-02标准存储")
    name = db.Column(db.String(64), nullable=False, comment='建筑名称，最长64个字符')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='建筑所属的小区')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='添加信息的用户id')


class GardenPictureKind(BaseModel):
    name = db.Column(db.String(64), primary_key=True, comment='小区照片种类名称')


class GardenPicture(BaseModel):
    id = db.Column(db.Integer, primary_key=True, comment='小区图片id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='小区id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='上传照片的用户的id')
    pictureKind = db.Column(db.String(64), db.ForeignKey('garden_picture_kind.name'), nullable=False, comment='小区图片种类')
    collectTime = db.Column(db.DateTime, nullable=False, comment='收集时间')
    syncTime = db.Column(db.DateTime, nullable=False, comment='同步时间')
    filePath = db.Column(db.Text, nullable=False, comment='文件储存路径')


class OtherPicture(BaseModel):
    id = db.Column(db.Integer, primary_key=True, comment='其他照片id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='小区id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='上传照片的用户的id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='收集时间')
    syncTime = db.Column(db.DateTime, nullable=False, comment='同步时间')
    filePath = db.Column(db.Text, nullable=False, comment='文件储存路径')
