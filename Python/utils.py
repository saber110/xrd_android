# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 自定义 flask 命令

import click
from functools import wraps
from flask import abort

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
            data = request.get_json()
            if 'token' not in data:
                return generate_result(1, message='请求参数错误，token不存在')
            token = data['token']
            user = User.verify_auth_token(token)
            if user is not None:
                return f(user=user, *args, **kwargs)
            else:
                abort(401)

    return wrapper
