# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 程序入口

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_redis import FlaskRedis
from flask_uploads import UploadSet, configure_uploads, IMAGES, patch_request_class

import config

app = Flask(__name__)
app.config['DEBUG'] = True
app.config['MAX_CONTENT_LENGTH'] = config.FILE_SIZE * 1024 * 1024
# 数据库连接配置
app.config[
    'SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://' + config.DB_USER + ':' + config.DB_PASSWORD + '@' + config.DB_HOST + ':' + config.DB_PORT + '/' + config.DB_NAME
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
# redis连接配置
app.config['REDIS_URL'] = 'redis://' + config.REDIS_HOST + ':' + config.REDIS_POST + '/0'
# 文件上传配置
app.config['UPLOADED_IMAGES_DEST'] = config.IMAGE_PATH
image_upload = UploadSet('images', IMAGES)
configure_uploads(app, image_upload)
patch_request_class(app)
db = SQLAlchemy(app)
redis_client = FlaskRedis(app)

import routes
import utils

if __name__ == '__main__':
    app.run()
