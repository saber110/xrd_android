# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:28
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 用户数据相关处理

from flask import request, Blueprint
from sqlalchemy.exc import SQLAlchemyError

from . import generate_result, generate_validator
from .. import config
from ..models.base_model import db
from ..models.user import User
from ..wraps import token_check, super_admin_required

user_bp = Blueprint('user', __name__, url_prefix=config.URL_Prefix + '/user')


@user_bp.route('/register', methods=['POST'])
@token_check
@super_admin_required
def register(*args, **kwargs):
    """
    用户注册接口，可用于批量导入也可用于单个注册
    """
    if request.method == 'POST':
        fail_ids = []
        data = request.get_json()
        schema = {
            'newUsers': {'type': 'list',
                         'schema': {'type': 'dict',
                                    'schema': {'iemi': {'type': 'string', 'maxlength': 17},
                                               'realName': {'type': 'string', 'maxlength': 32},
                                               'phoneNumber': {'type': 'string', 'maxlength': 11},
                                               'password': {'type': 'string', 'maxlength': 30}}}}
        }
        v = generate_validator(schema)
        if not v(data):  # 对请求数据格式进行校验
            return generate_result(1, data=v.errors)
        new_users = data['newUsers']
        for index, val in enumerate(new_users):
            try:
                user = User(**val)
                user.set_password(user.password)
                db.session.add(user)
                db.session.commit()
            except SQLAlchemyError:
                db.session.rollback()
                fail_ids.append(index)
        return generate_result(0, data={'fail_ids': fail_ids})


@user_bp.route('/login', methods=['POST'])
def login():
    """
    用户登录接口
    """
    if request.method == 'POST':
        data = request.get_json()
        schema = {
            'iemi': {'type': 'string', 'maxlength': 17},
            'password': {'type': 'string', 'maxlength': 30}
        }
        v = generate_validator(schema)
        if not v(data):
            return generate_result(1, data=v.errors)
        user = User.query.filter_by(iemi=data['iemi']).first()

        # 判断用户是否存在
        if user is None:
            return generate_result(2, '该用户不存在或密码错误')
        if not user.check_password(data['password']):
            return generate_result(2, '该用户不存在或密码错误')
        return generate_result(0, data={'token': str(user.generate_auth_token(), encoding='utf-8')})


@user_bp.route('/refresh_token', methods=['POST'])
@token_check
def refresh_token(user_id: int, *args, **kwargs):
    """
    更新token接口
    """
    user = User.query.get(user_id)
    return generate_result(0, data={'token': str(user.generate_auth_token(), encoding='utf-8')})


@user_bp.route('/all_info', methods=['POST'])
@token_check
@super_admin_required
def all_info(*args, **kwargs):
    """
    获取用户信息列表
    """
    user_info_list = [i.to_dict for i in User.query.all()]
    for user_info in user_info_list:
        del user_info['password']
    return generate_result(0, '获取用户列表成功', {'all_info': user_info_list})


@user_bp.route('/update', methods=['POST'])
@token_check
def update(user_id: int, *args, **kwargs):
    """
    用户更新个人信息
    :param user_id: 用户id
    """
    data = request.get_json()
    schema = {
        "realName": {'type': 'string', 'maxlength': 32},
        "phoneNumber": {'type': 'string'}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    user = User.query.get(user_id)
    user.realName = data['realName']
    user.phoneNumber = data['phoneNumber']
    try:
        db.session.add(user)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        return generate_result(2, '更新信息失败')
    return generate_result(0, '更新用户信息成功')


@user_bp.route('/super_update', methods=['POST'])
@token_check
@super_admin_required
def super_update(*args, **kwargs):
    """
    超级管理员批量更新用户信息
    """
    data = request.get_json()
    schema = {
        'userInfo': {'type': 'list',
                     'schema': {'type': 'dict',
                                'schema': {'id': {'type': 'integer', 'min': 1},
                                           'realName': {'type': 'string', 'maxlength': 32},
                                           'phoneNumber': {'type': 'string', 'maxlength': 11},
                                           'password': {'type': 'string', 'maxlength': 30},
                                           'permission': {'type': 'integer', 'min': 0}}}}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1, data=v.errors)
    user_info = data['userInfo']
    fail_ids = []

    for index, val in enumerate(user_info):
        try:
            user = User.query.get(val['id'])
            user.update(**val)
            user.reset_password(user.password)
            db.session.add(user)
            db.session.commit()
        except SQLAlchemyError:
            db.session.rollback()
            # 回退redis
            User.redis_del(val['id'])
            fail_ids.append(index)
    return generate_result(0, data={'fail_ids': fail_ids})


@user_bp.route('/reset_password', methods=['POST'])
@token_check
def reset_password(user_id: int, *args, **kwargs):
    """
    用户更新密码接口
    :return:
    """
    data = request.get_json()
    schema = {
        'oldPassword': {'type': 'string', 'maxlength': 64},
        'newPassword': {'type': 'string', 'maxlength': 64}
    }
    v = generate_validator(schema)
    if not v(data):
        return generate_result(1)
    try:
        user = User.query.get(user_id)
        if not user.check_password(data['oldPassword']):
            return generate_result(2, '密码错误')
        user.reset_password(data['newPassword'])
        db.session.add(user)
        db.session.commit()
    except SQLAlchemyError:
        db.session.rollback()
        # 回退redis
        User.redis_del(user_id)
        return generate_result(2, '数据更新失败')
    return generate_result(0, '更新密码成功')
