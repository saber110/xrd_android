# -*- coding: utf-8 -*-
# @Time : 2019/11/30 14:55
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 小区数据导入表

from .base_model import db, BaseModel


class GardenImportInfo(BaseModel):
    """
    小区信息导入表
    """
    id = db.Column(db.Integer, db.ForeignKey('garden.id'), primary_key=True, comment='小区id')
    boundaryStreetSign = db.Column(db.Text, comment='界址街牌')
    buildingNumbers = db.Column(db.Integer, comment='总套数')
    constructionUnit = db.Column(db.Text, comment='施工单位')
    volumeRate = db.Column(db.Text, comment='容_积_率')
    buildingDensity = db.Column(db.Text, comment='建筑密度')
    developer = db.Column(db.Text, comment='开_发_商')
    greeningRate = db.Column(db.Text, comment='绿_化_率')
    occupancyRate = db.Column(db.Text, comment='入_住_率')
    landArea = db.Column(db.Text, comment='土地面积')
    constructionScale = db.Column(db.Text, comment='建筑规模')
    residentialArea = db.Column(db.Text, comment='住宅面积')
    salesAgent = db.Column(db.Text, comment='销售代理')
    salesAddress = db.Column(db.Text, comment='售楼地址')
    tuQiu = db.Column(db.Text, comment='图_丘_号')
    salesPhone = db.Column(db.Text, comment='售楼电话')
    salesTime = db.Column(db.Text, comment='销售时间')
    mapSource = db.Column(db.Text, comment='地图来源')
    positioningCoordinates = db.Column(db.Text, comment='定位坐标')
    projectDescription = db.Column(db.Text, comment='项目简介')
    outEnvironment = db.Column(db.Text, comment='区外环境')
    otherEffects = db.Column(db.Text, comment='其他影响')
    businessDistrictDistance = db.Column(db.Text, comment='商圈距离')
    infrastructure = db.Column(db.Text, comment='基础设施')
    insideRoad = db.Column(db.Text, comment='内部道路')
    zoneEnvironment = db.Column(db.Text, comment='区内环境')
    gardenClubhouse = db.Column(db.Text, comment='小区会所')
    numberOfBerths = db.Column(db.Integer, comment='泊位数量')
    berthType = db.Column(db.Text, comment='泊位类型')
    berthRatio = db.Column(db.Text, comment='泊位配比')
    businessPackage = db.Column(db.Text, comment='商业配套')
    propertyCosts = db.Column(db.Text, comment='物业费用')
