# -*- coding: utf-8 -*-
from app import app
from config import URL_Prefix
from controllers import user, administration, data


def add_url(rule, function, methods):
    """

    :param rule: the URL rule as string
    :param function: the function to call when serving a request to the
                     provided endpoint
    :param methods: methods
                    is a list of methods this rule should be limited
                    to (``GET``, ``POST`` etc.).
    """
    app.add_url_rule(URL_Prefix + rule, view_func=function, methods=methods, endpoint=rule)


add_url('/user/register', user.register, methods=['POST'])
add_url('/user/login', user.login, methods=['POST'])
add_url('/user/refresh_token', user.refresh_token, methods=['POST'])
add_url('/administration/district', administration.district, methods=['POST'])
add_url('/administration/street', administration.street, methods=['POST'])
add_url('/administration/community', administration.community, methods=['POST'])
add_url('/administration/garden', administration.garden, methods=['POST'])
add_url('/data/garden', data.garden, methods=['POST'])
add_url('/data/building', data.building, methods=['POST'])
add_url('/data/map', data.map_data, methods=['POST'])


@app.route('/')
def hello():
    return 'hello world'
