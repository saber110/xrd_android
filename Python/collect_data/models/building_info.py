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
    buildingName = db.Column(db.Text, nullable=False, comment='建筑名')
    buildingAlias = db.Column(db.Text, comment='楼栋别名')
    buildingKind = db.Column(db.Text, comment='楼栋类别')
    isVilla = db.Column(db.String(1), comment='是否是别墅')
    firstFloorDescription = db.Column(db.String(64), db.ForeignKey('first_floor_kind.kind'), comment='一楼情况说明')
    firstFloorOtherDescription = db.Column(db.Text, comment='一楼其它情况备注')
    buildingFloorInfo = db.Column(db.Text, comment='楼栋层数情况')
    floorOverGround = db.Column(db.Integer, comment='地上总层')
    floorUnderGround = db.Column(db.Integer, comment='地下总层')
    propertyKind = db.Column(db.Text, comment='物业分类')
    buildingStructure = db.Column(db.Text, comment='建筑结构')
    oneLiftInfo = db.Column(db.Text, comment='一梯户数情况')
    numberOfUnit = db.Column(db.Integer, comment='单元数')
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
    roofTopTerrace = db.Column(db.String(1), comment='有无顶楼露台')
    roofTopAttic = db.Column(db.String(1), comment='有无顶楼阁楼')
    roofTopLayer = db.Column(db.String(1), comment='有无顶楼跃层')
    otherInfo = db.Column(db.Text, comment='楼栋调查表其他备注信息')
