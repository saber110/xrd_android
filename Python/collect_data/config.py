# -*- coding: utf-8 -*-
# @Time : 2019/11/11 20:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 开发config

# 文件上传大小限制
MAX_CONTENT_LENGTH = 16 * 1024 * 1024
# 数据库连接配置
SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://root:V1I#CvyACmM2^0hY@139.199.8.103:9999/dev'
# 不自动提交
SQLALCHEMY_TRACK_MODIFICATIONS = False
# redis 配置
REDIS_URL = 'redis://139.199.8.103:9998/0'
# 文件上传配置
UPLOADED_IMAGES_DEST = 'D:\project\Picture'
# 压缩图片的最大大小，单位kb
COMPRESSED_SIZE = 500
# 签署token的32位密钥
SECRET_KEY = 'WkX@Z!0JpF4sA0db*MkRFy&Lkv2s1LOa'
# token过期时间，单位秒
EXPIRATION = 60 * 60 * 24
# url前缀配置
URL_Prefix = '/api/v1'
