# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:09
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 行政区数据获取

import openpyxl as excel
from flask import request, Blueprint
from openpyxl.utils.exceptions import InvalidFileException
from sqlalchemy.exc import DBAPIError

from . import generate_result
from .. import config
from ..models.base_model import db
from ..models.community import Community
from ..models.district import District
from ..models.garden import Garden
from ..models.street import Street
from ..wraps import token_check, super_admin_required

administration_bp = Blueprint('administration', __name__, url_prefix=config.URL_Prefix + '/administration')


def have_empty_str(the_list_like) -> bool:
    if None in the_list_like:
        return True
    for item in the_list_like:
        if item.strip() == '':
            return True
    return False


@administration_bp.route('/district', methods=['POST'])
@token_check
def district(*args, **kwargs):
    districts = District.query.all()
    data = {'districts': [i.to_dict for i in districts]}
    return generate_result(0, '获取行政区数据成功', data)


@administration_bp.route('/street', methods=['POST'])
@token_check
def street(*args, **kwargs):
    data = request.get_json()
    if 'districtId' in data:
        districtId = data['districtId']
        streets = Street.query.filter_by(districtId=districtId).all()
        result_list = []
        for i in streets:
            the_dict = i.to_dict
            the_dict.pop('districtId')
            result_list.append(the_dict)
        return generate_result(0, '获取街道数据成功', {'streets': result_list})


@administration_bp.route('/community', methods=['POST'])
@token_check
def community(*args, **kwargs):
    data = request.get_json()
    if 'streetId' in data:
        streetId = data['streetId']
        communities = Community.query.filter_by(streetId=streetId).all()
        result_list = []
        for i in communities:
            the_dict = i.to_dict
            the_dict.pop('streetId')
            result_list.append(the_dict)
        return generate_result(0, '获取社区数据成功', {'communities': result_list})


@administration_bp.route('/garden', methods=['POST'])
@token_check
def garden(*args, **kwargs):
    data = request.get_json()
    if 'communityId' in data:
        community_id = data['communityId']
        gardens = Garden.query.filter_by(communityId=community_id).all()
        result_list = []
        for i in gardens:
            the_dict = i.to_dict
            the_dict.pop('communityId')
            result_list.append(the_dict)
        return generate_result(0, '获取小区数据成功', {'gardens': result_list})


@administration_bp.route('/import_data', methods=['POST'])
@token_check
@super_admin_required
def import_data(*args, **kwargs):
    """
    从excel文件导入行政区数据
    :return:
    """
    try:
        file = request.files['file']
    except KeyError:
        return generate_result(1)
    try:
        table = excel.load_workbook(file, read_only=True)
    except InvalidFileException:
        return generate_result(2, '仅支持.xlsx格式的文件')
    sheet_name_list = table.sheetnames
    fail_dict = {}
    if 'district' in sheet_name_list:
        sheet = table['district']
        district_fail_row = []
        for index, row in enumerate(sheet.iter_rows(min_row=2, max_col=1, values_only=True)):
            if have_empty_str(row):
                break  # 以防出现excel末尾存在大量空值
            the_district = District(name=row[0])
            try:
                db.session.add(the_district)
                db.session.commit()
            except DBAPIError:
                district_fail_row.append(index + 1)
        fail_dict['district'] = district_fail_row
    if 'district_street' in sheet_name_list:
        sheet = table['district_street']
        street_fail_row = []
        for index, row in enumerate(sheet.iter_rows(min_row=2, max_col=2, values_only=True)):
            if have_empty_str(row):
                break
            the_district = District.query.filter_by(name=row[0]).first()
            if the_district is not None:
                the_street = Street(districtId=the_district.id, name=row[1])
                try:
                    db.session.add(the_street)
                    db.session.commit()
                    continue
                except DBAPIError:
                    street_fail_row.append(index + 1)
            street_fail_row.append(index + 1)
        fail_dict['district_street'] = street_fail_row
    if 'district_street_community' in sheet_name_list:
        sheet = table['district_street_community']
        community_fail_row = []
        for index, row in enumerate(sheet.iter_rows(min_row=2, max_col=3, values_only=True)):
            if have_empty_str(row):
                break
            the_district = District.query.filter_by(name=row[0]).first()
            if the_district is not None:
                the_street = Street.query.filter_by(districtId=the_district.id, name=row[1]).first()
                if the_street is not None:
                    the_community = Community(streetId=the_street.id, name=row[2])
                    try:
                        db.session.add(the_community)
                        db.session.commit()
                        continue
                    except DBAPIError:
                        community_fail_row.append(index + 1)
            community_fail_row.append(index + 1)
        fail_dict['district_street_community'] = community_fail_row
    if 'district_street_community_garden' in sheet_name_list:
        sheet = table['district_street_community_garden']
        garden_fail_row = []
        for index, row in enumerate(sheet.iter_rows(min_row=2, max_col=4, values_only=True)):
            if have_empty_str(row):
                break
            the_district = District.query.filter_by(name=row[0]).first()
            if the_district is not None:
                the_street = Street.query.filter_by(districtId=the_district.id, name=row[1]).first()
                if the_street is not None:
                    the_community = Community.query.filter_by(streetId=the_street.id, name=row[2]).first()
                    if the_community is not None:
                        the_garden = Garden(communityId=the_community.id, streetId=the_street.id,
                                            districtId=the_district.id, name=row[3])
                        try:
                            db.session.add(the_garden)
                            db.session.commit()
                            continue
                        except DBAPIError:
                            garden_fail_row.append(index + 1)
            garden_fail_row.append(index + 1)

    return generate_result(0, '导入数据成功', {'fail_rows': fail_dict})
