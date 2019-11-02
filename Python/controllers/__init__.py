# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : controller中公用的函数

import cerberus

__all__ = ['user', 'generate_result', 'generate_validator']


def generate_result(code: int, message=None, data=None) -> dict:
    """
    生成返回json数据
    :param code: 返回码
    :param message: 返回信息
    :param data: 返回数据
    :return:
    """
    if data is None:
        data = {}
    if message is None:
        if code == 1:
            message = '请求参数有误'
        elif code == 2:
            message = '该用户不存在或密码错误'
        else:
            message = '请求成功'
    return {'code': code, 'message': message, 'data': data}


def generate_validator(schema: dict) -> cerberus.Validator:
    """

    :param schema:
    :return:
    """
    v = cerberus.Validator(schema, allow_unknown=True, require_all=True)
    return v
