package com.example.prashanth.porterdemo.rest;

import com.example.prashanth.porterdemo.eventbus.EventbusImpl;
import com.example.prashanth.porterdemo.eventbus.IEventbus;
import com.example.prashanth.porterdemo.threading.BusinessExecutor;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

import org.json.JSONObject;

public abstract class BaseJsonHttpCallBack extends HttpCallBack<JSONObject> {

    private static final String LOG_TAG = "BaseJsonHttpCallBack";

    private IEventbus mEventBus = EventbusImpl.getInstance();
    private IBusinessExecutor mBusinessExecutor = BusinessExecutor.getInstance();

    @Override
    void onFailure(final String errorMessage) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                onError(errorMessage, null);
            }
        });
    }

    @Override
    void success(final JSONObject response, final long currentServerTime) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                if (response == null) {
                    onError("Wrong response", null);
                    return;
                }
                onSuccess(response, currentServerTime);
            }
        });
    }

    private String getErrorMessage(JSONObject errorData){

        return null;
    }

    @Override
    void onReceiveEmptyData(final JSONObject errorData) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {

                onEmptyData(errorData);
            }
        });
    }

    protected abstract void onSuccess(JSONObject result, long currentServerTime);

    protected abstract void onError(String message, JSONObject jsonResponse);

    protected abstract void onEmptyData(JSONObject jsonResponse);
}