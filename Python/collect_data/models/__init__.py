# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:35
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : model 初始化的一些操作

import click
from flask import Flask

from .base_model import db
from .user import redis_client


def init_app(app: Flask):
    db.init_app(app)
    redis_client.init_app(app)

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
