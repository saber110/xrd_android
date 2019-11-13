# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:28
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 用户数据相关处理

from flask import request, Blueprint
from sqlalchemy.exc import DBAPIError

from . import generate_result, generate_validator
from .. import config
from ..models.base_model import db
from ..models.user import User
from ..wraps import token_check

user_bp = Blueprint('user', __name__, url_prefix=config.URL_Prefix + '/user')


@user_bp.route('/register', methods=["POST"])
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


@user_bp.route('/login', methods=["POST"])
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


@user_bp.route('/refresh_token', methods=["POST"])
@token_check
def refresh_token(user_id: int, *args, **kwargs):
    """
    更新token接口
    """
    user = User.query.get(user_id)
    return generate_result(0, data={'token': str(user.generate_auth_token(), encoding='utf-8')})
