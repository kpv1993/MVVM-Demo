package com.example.prashanth.porterdemo.engine;

import com.example.prashanth.porterdemo.rest.HttpCall;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestInterface {

    @GET("users/serviceability")
    HttpCall<JSONObject> getServiceability();

    @GET("vehicles/cost")
    HttpCall<JSONObject> getCost(@Query("lat") double latitude,
                                     @Query("lng")double longitude);

    @GET("vehicles/eta")
    HttpCall<JSONObject> getEta(@Query("lat") double latitude,
                                 @Query("lng")double longitude);

}
