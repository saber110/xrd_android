# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:07
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 路由和控制器部分的内容

from urllib.parse import quote

import cerberus
from flask import Flask, send_file
from flask_uploads import UploadSet, configure_uploads, IMAGES, patch_request_class

image_upload: UploadSet = UploadSet('images', IMAGES)


def init_app(app: Flask):
    from .administration import administration_bp
    from .user import user_bp
    from .data import data_bp
    from .get_data import get_data_bp
    app.register_blueprint(administration_bp)
    app.register_blueprint(user_bp)
    app.register_blueprint(data_bp)
    app.register_blueprint(get_data_bp)
    configure_uploads(app, image_upload)
    patch_request_class(app)


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
            message = '参数正确但数据不存在或验证失败'
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


def my_send_file(filename_or_fp, mimetype=None, attachment_filename=None):
    rv = send_file(filename_or_fp, mimetype)
    if attachment_filename is not None:
        attachment_filename = quote(attachment_filename)
        rv.headers['Content-Disposition'] = f"attachment;filename*=utf-8''{attachment_filename}"
    return rv
