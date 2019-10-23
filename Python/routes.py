# -*- coding: utf-8 -*-
from app import app
from config import URL_Prefix
from controllers import user


def add_url(rule, function, methods):
    """

    :param rule: the URL rule as string
    :param function: the function to call when serving a request to the
                     provided endpoint
    :param methods: methods
                    is a list of methods this rule should be limited
                    to (``GET``, ``POST`` etc.).
    """
    app.add_url_rule(URL_Prefix + rule, view_func=function, methods=methods)


add_url('/user/register', user.register, methods=['POST'])
