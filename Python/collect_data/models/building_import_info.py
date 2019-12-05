# -*- coding: utf-8 -*-
# @Time : 2019/11/30 19:29
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 楼栋导入信息表

from .base_model import db, BaseModel


class BuildingImportInfo(BaseModel):
    """
    楼栋导入信息表
    """
    id = db.Column(db.Integer, db.ForeignKey('building_info.id'), primary_key=True, comment='楼栋id')
    userId = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False, comment='提交信息的用户id')
    collectTime = db.Column(db.DateTime, nullable=False, comment='信息采集时间')
    buildingEastTo = db.Column(db.Text, comment='楼幢东至')
    buildingWestTo = db.Column(db.Text, comment='楼幢西至')
    buildingNorthTo = db.Column(db.Text, comment='楼幢北至')
    buildingSouthTo = db.Column(db.Text, comment='楼幢南至')
    allBuildingNumbers = db.Column(db.Integer, comment='总套数')
    housingHeight = db.Column(db.Text, comment='住房层高')
    decorationDescription = db.Column(db.Text, comment='装修描述')
    houseType = db.Column(db.Text, comment='户型分类')
    tuQiu = db.Column(db.Text, comment='图_丘_号')
    auxiliaryRoomUse = db.Column(db.Text, comment='辅房用途')
    underGroundGarage = db.Column(db.Text, comment='地下车库')
    constructionArea = db.Column(db.Text, comment='建筑面积')
    haveRoomRate = db.Column(db.Text, comment='得_房_率')
    architecturalStyle = db.Column(db.Text, comment='建筑风格')
    equipmentAndFacilities = db.Column(db.Text, comment='设备设施')
    exteriorWallFinishes = db.Column(db.Text, comment='外墙饰面')
    fireFacilities = db.Column(db.Text, comment='消防设施')
    elevatorFacilities = db.Column(db.Text, comment='电梯设施')
    airConditioningFacilities = db.Column(db.Text, comment='空调设施')
    hotWater = db.Column(db.Text, comment='有无热水')
    lobbyEntrance = db.Column(db.Text, comment='大堂入口')
    elevatorLobby = db.Column(db.Text, comment='电梯大堂')
    publicParts = db.Column(db.Text, comment='公共部位')
    ventilatedLighting = db.Column(db.Text, comment='通风采光')
    businessRoom = db.Column(db.Text, comment='营_业_房')
    usage = db.Column(db.Text, comment='使用状况')
    outsideView = db.Column(db.Text, comment='幢外景观')
    centralGarden = db.Column(db.Text, comment='中心花园')
    unfavorableFacilities = db.Column(db.Text, comment='不利设施')
    soundPolution = db.Column(db.Text, comment='噪声污染')
    buildingSpacing = db.Column(db.Text, comment='楼幢间距')
    threeAdventSituation = db.Column(db.Text, comment='三临情况')
    otherDisadvantages = db.Column(db.Text, comment='其他不利')
    otherAdvantages = db.Column(db.Text, comment='其他有利')
    buildingEvaluation = db.Column(db.Text, comment='楼幢评价')
    mapSource = db.Column(db.Text, comment='地图来源')
    positioningCoordinates = db.Column(db.Text, comment='定位坐标')
