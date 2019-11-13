# -*- coding: utf-8 -*-
# @Time : 2019/11/12 20:42
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 自定义装饰器

from functools import wraps

from flask import abort, request

from .models.user import User
from .routes import generate_result


def get_token():
    """
    从request中获取token
    :return: token
    """
    try:
        data = request.get_json()
        token = data['token']
    except (KeyError, TypeError):
        try:
            token = request.form['token']
        except KeyError:
            return generate_result(1, message='请求参数错误，token不存在')
    return token


def token_check(f):
    """
    token 检查装饰器
    """

    @wraps(f)
    def wrapper(*args, **kwargs):
        if request.method == 'POST':
            token = get_token()
            user_id = User.verify_auth_token(token)
            if user_id is not None:
                return f(user_id=int(user_id), *args, **kwargs)
            else:
                abort(401)

    return wrapper


def admin_required(f):
    """
    管理员权限检查装饰器
    :param f:
    """

    @wraps(f)
    def wrapper(*args, **kwargs):
        if request.method == 'POST':
            token = get_token()
            if User.verify_permission(token, 1):
                return f(*args, **kwargs)
            abort(401)

    return wrapper


def super_admin_required(f):
    """
    超级管理员权限检查装饰器
    :param f:
    """

    @wraps(f)
    def wrapper(*args, **kwargs):
        if request.method == 'POST':
            token = get_token()
            if User.verify_permission(token, 2):
                return f(*args, **kwargs)
            abort(401)

    return wrapper
