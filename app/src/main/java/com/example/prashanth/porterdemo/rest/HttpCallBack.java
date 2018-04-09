package com.example.prashanth.porterdemo.rest;

import org.json.JSONObject;

import retrofit2.Response;

public abstract class HttpCallBack<T> {

    abstract void onFailure(String errorMessage);

    abstract void onReceiveEmptyData(JSONObject errorData);

    abstract void success(T response, long serverTime);

}
