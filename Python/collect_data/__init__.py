# -*- coding: utf-8 -*-
# @Time : 2019/11/11 19:30
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 项目入口

from flask import Flask
from flask_cors import CORS


def create_app():
    app = Flask(__name__)
    CORS(app)
    app.config.from_pyfile('config.py')
    from . import models, routes
    models.init_app(app)
    routes.init_app(app)
    return app
