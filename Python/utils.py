# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 自定义 flask 命令

import click
from functools import wraps
from flask import abort
import math

from app import app, db
from models import User
from controllers import generate_result


@app.cli.command()
@click.option('--drop', is_flag=True, help='Create after drop.')
def init_db(drop):
    """
    根据定义model初始数据库
    """
    if drop:
        db.drop_all()
    db.create_all()
    click.echo('Initialized database')


@app.cli.command()
def drop_db():
    """
    删除所有表
    """
    db.drop_all()


def token_check(f):
    """
    token 检查函数
    """

    @wraps(f)
    def wrapper(*args, **kwargs):
        from flask import request
        if request.method == 'POST':
            try:
                data = request.get_json()
                token = data['token']
            except (KeyError, TypeError):
                try:
                    token = request.form['token']
                except KeyError:
                    return generate_result(1, message='请求参数错误，token不存在')
            user_id = User.verify_auth_token(token)
            if user_id is not None:
                return f(user_id=user_id, *args, **kwargs)
            else:
                abort(401)

    return wrapper


def gcj02_to_bd09(lng, lat):
    """
    火星坐标系(GCJ-02)转百度坐标系(BD-09)
    谷歌、高德、腾讯——>百度
    :param lng:火星坐标经度
    :param lat:火星坐标纬度
    :return:
    """
    x_pi = math.pi * 3000.0 / 180.0
    z = math.sqrt(lng * lng + lat * lat) + 0.00002 * math.sin(lat * x_pi)
    theta = math.atan2(lat, lng) + 0.000003 * math.cos(lng * x_pi)
    bd_lng = z * math.cos(theta) + 0.0065
    bd_lat = z * math.sin(theta) + 0.006
    return bd_lng, bd_lat


def bd09_to_gcj02(bd_lon, bd_lat):
    """
    百度坐标系(BD-09)转火星坐标系(GCJ-02)
    百度——>谷歌、高德、腾讯
    :param bd_lat:百度坐标纬度
    :param bd_lon:百度坐标经度
    :return:转换后的坐标列表形式
    """
    x_pi = math.pi * 3000.0 / 180.0
    x = bd_lon - 0.0065
    y = bd_lat - 0.006
    z = math.sqrt(x * x + y * y) - 0.00002 * math.sin(y * x_pi)
    theta = math.atan2(y, x) - 0.000003 * math.cos(x * x_pi)
    gg_lng = z * math.cos(theta)
    gg_lat = z * math.sin(theta)
    return gg_lng, gg_lat
