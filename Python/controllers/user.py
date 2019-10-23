# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : user相关处理函数

from flask import request
from sqlalchemy.exc import DBAPIError
from werkzeug.security import generate_password_hash, check_password_hash

from app import db
from models import User
from . import generate_result, generate_validator


def register():
    """
    用户注册接口，可用于批量导入也可用于单个注册
    """
    if request.method == "POST":
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
        if not v.validate(data):  # 对请求数据格式进行校验
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
