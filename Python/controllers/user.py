# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : user相关处理函数

from flask import request
from sqlalchemy.exc import DBAPIError

from app import db
from models import User
from . import generate_result, generate_validator
from utils import token_check


def register():
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
            except DBAPIError:
                fail_ids.append(index)
        return generate_result(0, data={'fail_ids': fail_ids})


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
            return generate_result(2)
        if not user.check_password(data['password']):
            return generate_result(2)
        return generate_result(0, data={'token': str(user.generate_auth_token(), encoding='utf-8')})


@token_check
def refresh_token(user: User):
    """
    更新token接口
    """
    return generate_result(0, data={'token': str(user.generate_auth_token(), encoding='utf-8')})
