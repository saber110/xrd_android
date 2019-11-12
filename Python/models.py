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


class BuildingPictureKind(BaseModel):
    name = db.Column(db.String(64), primary_key=True, comment='建筑照片种类名称')


class BuildingPicture(BaseModel):
    id = db.Column(db.Integer, primary_key=True, comment='楼栋照片id')
    buildingId = db.Column(db.Integer, db.ForeignKey('building_info.id'), nullable=False, comment='照片对应的楼栋id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='上传照片的用户的id')
    pictureKind = db.Column(db.String(64), db.ForeignKey('building_picture_kind.name'), nullable=False,
                            comment='小区图片种类')
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


class GardenBaseInfo(BaseModel):
    """
    小区概况表
    """
    id = db.Column(db.Integer, db.ForeignKey('garden.id'), primary_key=True, comment='小区id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='提交信息的用户id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='信息采集时间')
    communityAlias = db.Column(db.Text, comment='社区别名')
    gardenAlias = db.Column(db.Text, comment='小区别名')
    gardenAlias2 = db.Column(db.Text, comment='小区别名2')
    gardenLocation = db.Column(db.Text, nullable=False, comment='小区坐落位置')
    gardenEastTo = db.Column(db.Text, nullable=False, comment='小区东至')
    gardenWestTo = db.Column(db.Text, nullable=False, comment='小区西至')
    gardenNorthTo = db.Column(db.Text, nullable=False, comment='小区北至')
    gardenSouthTo = db.Column(db.Text, nullable=False, comment='小区南至')
    regionalLocation = db.Column(db.Text, nullable=False, comment='区域位置')
    houseStatus = db.Column(db.Text, nullable=False, comment='楼盘状态')
    gardenKind = db.Column(db.Text, nullable=False, comment='小区类型')
    buildingKind = db.Column(db.Text, comment='建筑类型')
    roomType = db.Column(db.Text, nullable=False, comment='房屋性质')
    buildingStructure = db.Column(db.Text, nullable=False, comment='建筑结构')
    houseNumber = db.Column(db.Integer, nullable=False, comment='住宅栋数')
    notHouseNumber = db.Column(db.Integer, comment='非住宅栋数')
    description = db.Column(db.Text, nullable=False, comment='栋数描述')
    buildYear = db.Column(db.Integer, nullable=False, comment='建成年份')
    setYear = db.Column(db.Integer, nullable=False, comment='设定年份')
    landStatus = db.Column(db.Text, nullable=False, comment='土地性质')
    right = db.Column(db.Text, nullable=False, comment='使用权')
    landGrade = db.Column(db.Text, nullable=False, comment='土地等级')
    askRecode = db.Column(db.Text, nullable=False, comment='询价记录')
    closed = db.Column(db.String(1), nullable=False, comment='是否封闭')
    managementKind = db.Column(db.Text, nullable=False, comment='物业管理分类')
    otherInfo = db.Column(db.Text, comment='小区概况表其它信息备注')
    neighborGarden = db.Column(db.Text, comment='相邻小区')
    mainRoad = db.Column(db.Text, comment='交通干道')
    roadGrade = db.Column(db.Text, nullable=False, comment='道路等级')
    busStation = db.Column(db.Text, comment='公交站名')
    busStationDistance = db.Column(db.Integer, comment='站点距离')
    baseBus = db.Column(db.Text, comment='普通公交')
    quickBus = db.Column(db.Text, comment='快速公交')
    busLines = db.Column(db.Integer, comment='线路条数')
    subwayStation = db.Column(db.Text, comment='地铁站')
    subwayDistance = db.Column(db.Integer, comment='地铁站距离')
    subwayLines = db.Column(db.Integer, comment='地铁线路条数')
    farmerMarket = db.Column(db.Text, comment='农贸市场')
    market = db.Column(db.Text, comment='超市商场')
    hospital = db.Column(db.Text, comment='医疗设施')
    bank = db.Column(db.Text, comment='金融设施')
    gym = db.Column(db.Text, comment='文体设施')
    organization = db.Column(db.Text, comment='行政机关')
    kindergarten = db.Column(db.Text, comment='幼儿园')
    primary = db.Column(db.Text, comment='小学')
    middle = db.Column(db.Text, comment='中学')
    college = db.Column(db.Text, comment='大学')
    attractions = db.Column(db.Text, comment='景点')
    park = db.Column(db.Text, comment='公园')
    streetNumber = db.Column(db.Text, comment='路牌号')
    businessArea = db.Column(db.Text, comment='所处商圈')
    locationArea = db.Column(db.Text, comment='所处板块')
    aroundUse = db.Column(db.Text, comment='周边利用')
    riversAndMountains = db.Column(db.Text, comment='河流山川')
    noisePollution = db.Column(db.Text, comment='噪音污染')
    airPollution = db.Column(db.Text, comment='空气污染')
    adverseFacilities = db.Column(db.Text, comment='不利设施')
    otherPollution = db.Column(db.Text, comment='其他污染')
    busyDegree = db.Column(db.Text, comment='繁华程度')
    relaxFacilities = db.Column(db.Text, comment='休闲设施')
    sportFacilities = db.Column(db.Text, comment='运动设施')
    securityFacilities = db.Column(db.Text, comment='安保设施')
    architecturalStyle = db.Column(db.Text, comment='建筑风格')
    gardenGreening = db.Column(db.Text, comment='小区绿化')
    gardenEvaluation = db.Column(db.Text, comment='小区评价')
    propertyCompany = db.Column(db.Text, comment='物业公司')


class FirstFloorKind(BaseModel):
    """
    楼栋一楼的可能情况
    """
    kind = db.Column(db.String(64), primary_key=True, comment='一楼可能的情况最多64个字符')


class BuildingInfo(BaseModel):
    """
    楼栋调查表
    """
    id = db.Column(db.Integer, primary_key=True, comment='楼栋调查表id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='楼栋所属的小区')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='提交信息的用户id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='信息采集时间')
    buildingName = db.Column(db.Text, nullable=False, comment='建筑名')
    buildingAlias = db.Column(db.Text, comment='楼栋别名')
    buildingKind = db.Column(db.Text, comment='楼栋类别')
    isVilla = db.Column(db.Boolean, comment='是否是别墅')
    firstFloorDescription = db.Column(db.String(64), db.ForeignKey('first_floor_kind.kind'), comment='一楼情况说明')
    firstFloorOtherDescription = db.Column(db.Text, comment='一楼其它情况备注')
    buildingFloorInfo = db.Column(db.Text, comment='楼栋层数情况')
    floorOverGround = db.Column(db.Integer, comment='地上总层')
    floorUnderGround = db.Column(db.Integer, comment='地下总层')
    propertyKind = db.Column(db.Text, comment='物业分类')
    buildingStructure = db.Column(db.Text, comment='建筑结构')
    oneLiftInfo = db.Column(db.Text, comment='一梯户数情况')
    oneLiftNumber = db.Column(db.Integer, comment='一梯几户')
    unitName = db.Column(db.Text, comment='单元名称')
    roomName = db.Column(db.Text, comment='室号名称')
    unitNumber = db.Column(db.Text, comment='单元号')
    floorNumber = db.Column(db.Text, comment='楼层号')
    floorDifferent = db.Column(db.Text, comment='楼层差异')
    beginFloor = db.Column(db.Integer, comment='住宅起始')
    palette = db.Column(db.Text, comment='画板')
    locationDescription = db.Column(db.Text, comment='部位说明')
    mainTowards = db.Column(db.Text, comment='主要朝向')
    planeLayout = db.Column(db.Text, comment='平面布局')
    completedYear = db.Column(db.Integer, comment='建成年份')
    decorationStandard = db.Column(db.Text, comment='装修标准')
    decorationYear = db.Column(db.Text, comment='装修时间')
    buildingProperty = db.Column(db.Text, comment='房产性质')
    buildingStatus = db.Column(db.Text, comment='楼栋状态')
    roofTopInfo = db.Column(db.Text, comment='顶楼情况')
    roofInfo = db.Column(db.Text, comment='屋面情况')
    roofTopTerrace = db.Column(db.Boolean, comment='有无顶楼露台')
    roofTopAttic = db.Column(db.Boolean, comment='有无顶楼阁楼')
    roofTopLayer = db.Column(db.Boolean, comment='有无顶楼跃层')
    otherInfo = db.Column(db.Text, comment='楼栋调查表其他备注信息')
