package com.example.prashanth.porterdemo.engine;

import android.util.Log;

import com.example.prashanth.porterdemo.eventbus.EventbusImpl;
import com.example.prashanth.porterdemo.eventbus.IEventbus;
import com.example.prashanth.porterdemo.events.IGetCostSuccessEngineEvent;
import com.example.prashanth.porterdemo.events.IGetEtaSuccessEngineEvent;
import com.example.prashanth.porterdemo.events.IGetServiceSuccessEngineEvent;
import com.example.prashanth.porterdemo.rest.BaseJsonHttpCallBack;
import com.example.prashanth.porterdemo.rest.HttpCall;
import com.example.prashanth.porterdemo.rest.ServerAddress;
import com.example.prashanth.porterdemo.rest.ServiceGenerator;
import com.example.prashanth.porterdemo.threading.BusinessExecutor;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

import org.json.JSONException;
import org.json.JSONObject;

public class Engine implements IEngine {

    private static final String TAG = "Engine";
    private IBusinessExecutor mBusinessExecutor;
    private IEventbus mEventBus;
    private static Engine sInstance = new Engine();
    private static String SERVICEABILITY = "serviceable";
    private static String COST = "cost";
    private static String ETA = "eta";

    public static Engine getInstance(){
        return sInstance;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        mBusinessExecutor = BusinessExecutor.getInstance();
        mEventBus = EventbusImpl.getInstance();
    }

    @Override
    public void getService() {
        Log.d(TAG, "getService");
        RestInterface vehicleService = ServiceGenerator.createService(
                RestInterface.class, ServerAddress.URL);

        HttpCall<JSONObject> httpCall = vehicleService.getServiceability();
        BaseJsonHttpCallBack callBack = new BaseJsonHttpCallBack() {
            @Override
            protected void onSuccess(final JSONObject result, long currentServerTime) {

                boolean isServiceable = false;
                if(result != null){
                    if(result.has(SERVICEABILITY)){
                        try {
                            isServiceable = result.getBoolean(SERVICEABILITY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "getService: onSuccess ");
                final boolean finalIsServiceable = isServiceable;
                mEventBus.post(new IGetServiceSuccessEngineEvent() {

                    @Override
                    public boolean isServiceable() {
                        return finalIsServiceable;
                    }
                });
            }

            @Override
            protected void onError(final String message, JSONObject jsonResponse) {
                Log.d(TAG, "getService: onError " + message);
//                mEventBus.post(new IAddVehicleFailureEngineEvent() {
//
//                });
            }

            @Override
            protected void onEmptyData(JSONObject jsonResponse) {
                Log.d(TAG, "getService: onEmptyData ");

            }
        };
        httpCall.enqueue(callBack);
    }

    @Override
    public void getCost(double lat, double lng) {
        Log.d(TAG, "getCost");
        RestInterface vehicleService = ServiceGenerator.createService(
                RestInterface.class, ServerAddress.URL);

        HttpCall<JSONObject> httpCall = vehicleService.getCost(lat, lng);
        BaseJsonHttpCallBack callBack = new BaseJsonHttpCallBack() {
            @Override
            protected void onSuccess(final JSONObject result, long currentServerTime) {

                Integer cost = null;
                if(result != null){
                    if(result.has(COST)){
                        try {
                            cost = result.getInt(COST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "getCost: onSuccess ");
                final Integer finalCost = cost;
                mEventBus.post(new IGetCostSuccessEngineEvent() {

                    @Override
                    public Integer getCost() {
                        return finalCost;
                    }
                });
            }

            @Override
            protected void onError(final String message, JSONObject jsonResponse) {
                Log.d(TAG, "getCost: onError " + message);
//                mEventBus.post(new IAddVehicleFailureEngineEvent() {
//
//                });
            }

            @Override
            protected void onEmptyData(JSONObject jsonResponse) {
                Log.d(TAG, "getCost: onEmptyData ");

            }
        };
        httpCall.enqueue(callBack);
    }

    @Override
    public void getEta(double lat, double lng) {
        Log.d(TAG, "getEta");
        RestInterface vehicleService = ServiceGenerator.createService(
                RestInterface.class, ServerAddress.URL);

        HttpCall<JSONObject> httpCall = vehicleService.getEta(lat, lng);
        BaseJsonHttpCallBack callBack = new BaseJsonHttpCallBack() {
            @Override
            protected void onSuccess(final JSONObject result, long currentServerTime) {

                Integer eta = null;
                if(result != null){
                    if(result.has(ETA)){
                        try {
                            eta = result.getInt(ETA);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "getEta: onSuccess ");
                final Integer finalEta = eta;
                mEventBus.post(new IGetEtaSuccessEngineEvent() {

                    @Override
                    public Integer getEta() {
                        return finalEta;
                    }
                });
            }

            @Override
            protected void onError(final String message, JSONObject jsonResponse) {
                Log.d(TAG, "getEta: onError " + message);
//                mEventBus.post(new IAddVehicleFailureEngineEvent() {
//
//                });
            }

            @Override
            protected void onEmptyData(JSONObject jsonResponse) {
                Log.d(TAG, "getEta: onEmptyData ");

            }
        };
        httpCall.enqueue(callBack);
    }

}
