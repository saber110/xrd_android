# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:57
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 小区基本信息表

from .base_model import db, BaseModel


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
    gardenLocation = db.Column(db.Text, comment='小区坐落位置')
    gardenEastTo = db.Column(db.Text, comment='小区东至')
    gardenWestTo = db.Column(db.Text, comment='小区西至')
    gardenNorthTo = db.Column(db.Text, comment='小区北至')
    gardenSouthTo = db.Column(db.Text, comment='小区南至')
    regionalLocation = db.Column(db.Text, comment='区域位置')
    houseStatus = db.Column(db.Text, server_default='存量', comment='楼盘状态')
    gardenKind = db.Column(db.Text, server_default='普通商品房', comment='小区类型')
    buildingKind = db.Column(db.Text, comment='建筑类型')
    roomType = db.Column(db.Text, comment='房屋性质')
    buildingStructure = db.Column(db.Text, comment='建筑结构')
    houseNumber = db.Column(db.Integer, comment='住宅栋数')
    notHouseNumber = db.Column(db.Integer, comment='非住宅栋数')
    description = db.Column(db.Text, comment='栋数描述')
    buildYear = db.Column(db.Integer, comment='建成年份')
    setYear = db.Column(db.Integer, comment='设定年份')
    landStatus = db.Column(db.Text, server_default='国有土地', comment='土地性质')
    right = db.Column(db.Text, server_default='出让', comment='使用权')
    landGrade = db.Column(db.Text, comment='土地等级')
    askRecode = db.Column(db.Text, comment='询价记录')
    closed = db.Column(db.String(1), comment='是否封闭')
    managementKind = db.Column(db.Text, comment='物业管理分类')
    beginPrice = db.Column(db.Text, comment='价格初判')
    otherInfo = db.Column(db.Text, comment='小区概况表其它信息备注')
    neighborGarden = db.Column(db.Text, comment='相邻小区')
    mainRoad = db.Column(db.Text, comment='交通干道')
    roadGrade = db.Column(db.Text, comment='道路等级')
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
    otherInfluences = db.Column(db.Text, comment='其他影响')
    busyDegree = db.Column(db.Text, comment='繁华程度')
    relaxFacilities = db.Column(db.Text, comment='休闲设施')
    sportFacilities = db.Column(db.Text, comment='运动设施')
    securityFacilities = db.Column(db.Text, comment='安保设施')
    architecturalStyle = db.Column(db.Text, comment='建筑风格')
    gardenGreening = db.Column(db.Text, comment='小区绿化')
    gardenEvaluation = db.Column(db.Text, comment='小区评价')
    propertyCompany = db.Column(db.Text, comment='物业公司')
