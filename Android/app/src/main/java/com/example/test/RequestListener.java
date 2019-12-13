package com.example.test;

import okhttp3.Response;

public interface RequestListener {
    void onSuccess(Response response);
    void onFailure(String info);
}
