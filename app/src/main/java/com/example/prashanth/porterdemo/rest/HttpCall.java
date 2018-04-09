package com.example.prashanth.porterdemo.rest;

import java.io.IOException;

import retrofit2.Response;

public interface HttpCall<T> {
    void cancel();
    void enqueue(HttpCallBack<T> callback);
    Response<T> execute() throws IOException;
    HttpCall<T> clone();
}
