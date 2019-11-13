# -*- coding: utf-8 -*-
# @Time : 2019/10/23 17:53
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 辅助函数

import math
import os

from PIL import Image

from . import config


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


def compress_image(origin_path: str, compressed_path: str, size: int):
    """
    将图片压缩至指定大小内到指定路径
    :param origin_path: 原始图片位置
    :param compressed_path: 压缩图片位置
    :param size: 图片大小
    :return:
    """
    origin_image = Image.open(origin_path)
    dir_name = os.path.dirname(compressed_path)
    os.makedirs(dir_name, exist_ok=True)
    origin_image.save(compressed_path)
    while True:
        file_size = os.path.getsize(compressed_path) / float(1024)
        if file_size < size:
            break
        rate = config.COMPRESSED_SIZE / file_size
        rate = math.sqrt(rate)
        image = Image.open(compressed_path)
        width, high = image.size
        image = image.resize((int(rate * width), int(rate * high)), Image.ANTIALIAS)
        image.save(compressed_path)
