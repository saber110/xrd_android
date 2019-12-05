# -*- coding: utf-8 -*-
# @Time : 2019/12/5 23:00
# @Author : 尹傲雄
# @contact : yinaoxiong@gmail.com
# @Desc : 生产环境运行
from gevent.pywsgi import WSGIServer

from collect_data import create_app

app = create_app()
http_server = WSGIServer(('0.0.0.0', 5000), app)
http_server.serve_forever()
