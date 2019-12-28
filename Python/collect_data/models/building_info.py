# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:58
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc :     楼栋调查表

from .base_model import db, BaseModel


class BuildingInfo(BaseModel):
    """
    楼栋调查表
    """
    id = db.Column(db.Integer, primary_key=True, comment='楼栋调查表id')
    gardenId = db.Column(db.Integer, db.ForeignKey('garden.id'), nullable=False, comment='楼栋所属的小区')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='提交信息的用户id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='信息采集时间')
    buildingName = db.Column(db.Text, comment='建筑名')
    buildingAlias = db.Column(db.Text, comment='楼栋别名')
    buildingKind = db.Column(db.Text, comment='楼栋类别')
    isVilla = db.Column(db.Text, comment='是否是别墅')
    overheadLayer = db.Column(db.Text, default='_', comment='架空层')
    aboveGroundGarage = db.Column(db.Text, default='_', comment='地上车库')
    utilityRoom = db.Column(db.Text, default='_', comment='杂物间')
    residential = db.Column(db.Text, default='_', comment='住宅')
    shop = db.Column(db.Text, default='_', comment='商铺')
    # firstFloorDescription = db.Column(db.String(64), db.ForeignKey('first_floor_kind.kind'), comment='一楼情况说明')
    firstFloorOtherDescription = db.Column(db.Text, comment='一楼其它情况备注')
    oneUnitLayers = db.Column(db.Integer, comment='一单元楼幢层数')
    twoUnitLayers = db.Column(db.Integer, comment='二单元楼幢层数')
    threeUnitLayers = db.Column(db.Integer, comment='三单元楼幢层数')
    fourUnitLayers = db.Column(db.Integer, comment='四单元楼幢层数')
    fiveUnitLayers = db.Column(db.Integer, comment='五单元楼幢层数')
    sixUnitLayers = db.Column(db.Integer, comment='六单元楼幢层数')
    sevenUnitLayers = db.Column(db.Integer, comment='七单元楼幢层数')
    eightUnitLayers = db.Column(db.Integer, comment='八单元楼幢层数')
    nightUnitLayers = db.Column(db.Integer, comment='九单元楼幢层数')
    tenUnitLayers = db.Column(db.Integer, comment='十单元楼幢层数')
    # buildingFloorInfo = db.Column(db.Text, comment='楼栋层数情况')
    floorOverGround = db.Column(db.Integer, comment='地上总层')
    floorUnderGround = db.Column(db.Integer, comment='地下总层')
    propertyKind = db.Column(db.Text, comment='物业分类')
    buildingStructure = db.Column(db.Text, comment='建筑结构')
    oneUnitHouseholds = db.Column(db.Integer, comment='一单元一梯户数')
    twoUnitHouseholds = db.Column(db.Integer, comment='二单元一梯户数')
    threeUnitHouseholds = db.Column(db.Integer, comment='三单元一梯户数')
    fourUnitHouseholds = db.Column(db.Integer, comment='四单元一梯户数')
    fiveUnitHouseholds = db.Column(db.Integer, comment='五单元一梯户数')
    sixUnitHouseholds = db.Column(db.Integer, comment='六单元一梯户数')
    sevenUnitHouseholds = db.Column(db.Integer, comment='七单元一梯户数')
    eightUnitHouseholds = db.Column(db.Integer, comment='八单元一梯户数')
    nightUnitHouseholds = db.Column(db.Integer, comment='九单元一梯户数')
    tenUnitHouseholds = db.Column(db.Integer, comment='十单元一梯户数')
    # oneLiftInfo = db.Column(db.Text, comment='一梯户数情况')
    numberOfUnit = db.Column(db.Integer, comment='单元数')
    oneLiftNumber = db.Column(db.Integer, comment='一梯几户')
    unitName = db.Column(db.Text, default='无', comment='单元名称')
    roomName = db.Column(db.Text, default='室', comment='室号名称')
    unitNumber = db.Column(db.Text, comment='单元号')
    floorNumber = db.Column(db.Text, comment='楼层号')
    floorDifferent = db.Column(db.Text, comment='楼层差异')
    beginFloor = db.Column(db.Integer, comment='住宅起始')
    shopLayer = db.Column(db.Integer, comment='店铺层数')
    palette = db.Column(db.Text, comment='画板')
    locationDescription = db.Column(db.Text, comment='部位说明')
    mainTowards = db.Column(db.Text, default='南', comment='主要朝向')
    planeLayout = db.Column(db.Text, default='一般', comment='平面布局')
    completedYear = db.Column(db.Integer, comment='建成年份')
    decorationStandard = db.Column(db.Text, default='毛坯房', comment='装修标准')
    decorationYear = db.Column(db.Integer, comment='装修时间')
    buildingProperty = db.Column(db.Text, default='商品房', comment='房产性质')
    buildingStatus = db.Column(db.Text, default='存量', comment='楼栋状态')
    roofTopInfo = db.Column(db.Text, default='平层', comment='顶楼情况')
    roofInfo = db.Column(db.Text, default='平屋顶', comment='屋面情况')
    roofTopTerrace = db.Column(db.Text, default='无', comment='有无顶楼露台')
    roofTopAttic = db.Column(db.Text, default='无', comment='有无顶楼阁楼')
    roofTopLayer = db.Column(db.Text, default='无', comment='有无顶楼跃层')
    otherInfo = db.Column(db.Text, comment='楼栋调查表其他备注信息')
    outsideViewScore = db.Column(db.Float, comment='小区外视野景观')
    centralGardenScore = db.Column(db.Float, comment='中心花园位置')
    ventilatedLightingScore = db.Column(db.Float, comment='通风采光情况')
    threeAdventSituationScore = db.Column(db.Float, comment='三临情况得分')
    surroundingsScore = db.Column(db.Float, comment='周围环境')
    buildingAppearanceScore = db.Column(db.Float, comment='建筑外观')
    otherAdvantagesScore = db.Column(db.Float, comment='其他有利')
    otherDisadvantagesScore = db.Column(db.Float, comment='其他不利')
    buildingLevel = db.Column(db.Float, comment='楼幢标准层等级初判')
    preliminaryPrice = db.Column(db.Text, comment='价格初判')
