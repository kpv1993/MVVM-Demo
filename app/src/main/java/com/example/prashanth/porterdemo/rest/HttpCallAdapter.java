package com.example.prashanth.porterdemo.rest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpCallAdapter<T> implements HttpCall<T> {

    private static final String TAG = "HttpCallAdapter";
    private final Call<T> call;
    private final Executor callbackExecutor;
    private final String accessToken;

    public HttpCallAdapter(Call<T> call, Executor callbackExecutor, String accessToken) {
        this.call = call;
        this.callbackExecutor = callbackExecutor;
        this.accessToken = accessToken;
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public void enqueue(final HttpCallBack<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final Response<T> response) {
                if(callbackExecutor != null){
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            processResponse(response, callback);
                        }
                    });
                } else {
                    processResponse(response, callback);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                final String message;
                message = "Please Check Back";
                if(callbackExecutor != null){
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            processFailure(message, callback);
                        }
                    });
                } else {
                    processFailure(message, callback);
                }
            }
        });
    }

    @Override
    public Response<T> execute() throws IOException {
        return call.execute();
    }

    @Override
    public HttpCall<T> clone() {
        return new HttpCallAdapter<>(call.clone(), callbackExecutor, accessToken);
    }

    private void processResponse(Response<T> response, HttpCallBack<T> callback) {
        int code = response.code();
        if (code >= 200 && code < 300) {
            Headers headers = response.headers();
            Date date = headers.getDate("Date");
            long serverTime = -1L;
            if(date!= null){
                serverTime = date.getTime();
            }
            callback.success(response.body(), serverTime);
        } else if (code == 401) {

        } else if(code >=500) {

//            callback.onFailure(SERVER_ERROR_MESSAGE);
        } else {
            JSONObject errorData = new JSONObject();
            try {
                errorData = new JsonResponseBodyConverter().convert(response.errorBody());
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }

        }
    }

    private String getErrorKey(JSONObject errorData){
        return null;
    }

    private String getErrorMessage(JSONObject errorData){

        return null;
    }

    private void processFailure(String message, HttpCallBack<T> callback) {
        callback.onFailure(message);
    }
}
