# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 自定义 flask 命令

import click

from app import app, db


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
