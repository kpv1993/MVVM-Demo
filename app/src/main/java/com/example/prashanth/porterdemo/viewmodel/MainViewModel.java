package com.example.prashanth.porterdemo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.prashanth.porterdemo.eventbus.IEventbus;
import com.example.prashanth.porterdemo.events.IGetCostFailureEngineEvent;
import com.example.prashanth.porterdemo.events.IGetCostSuccessEngineEvent;
import com.example.prashanth.porterdemo.events.IGetEtaFailureEngineEvent;
import com.example.prashanth.porterdemo.events.IGetEtaSuccessEngineEvent;
import com.example.prashanth.porterdemo.events.IGetServiceFailureEngineEvent;
import com.example.prashanth.porterdemo.events.IGetServiceSuccessEngineEvent;
import com.example.prashanth.porterdemo.factory.EngineFactory;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

import org.greenrobot.eventbus.Subscribe;

public class MainViewModel extends BaseViewModel {

    private static final String TAG = "MainViewModel";

    public MainViewModel(IEventbus eventbus, IBusinessExecutor businessExecutor) {
        super(eventbus, businessExecutor);
    }

    private MutableLiveData<Boolean> getServiceObservable = new MutableLiveData<Boolean>();
    private MutableLiveData<Integer> getCostObservable = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> getEtaObservable = new MutableLiveData<Integer>();

    public LiveData<Boolean> getServiceObservable() {
        return getServiceObservable;
    }

    public LiveData<Integer> getCostObservable() {
        return getCostObservable;
    }

    public LiveData<Integer> getEtaObservable() {
        return getEtaObservable;
    }

    public void getService() {
        businessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                EngineFactory.getEngine().getService();
            }
        });
    }

    public void getCost(final double lat, final double lng) {
        businessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                EngineFactory.getEngine().getCost(lat, lng);
            }
        });
    }

    public void getEta(final double lat, final double lng) {
        businessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                EngineFactory.getEngine().getEta(lat, lng);
            }
        });
    }

    @Subscribe
    public void onGetServiceSuccess(final IGetServiceSuccessEngineEvent event){
        Log.d(TAG, "onGetServiceSuccess");
        getServiceObservable.postValue(event.isServiceable());
    }

    @Subscribe
    public void onGetServiceFailure(final IGetServiceFailureEngineEvent event){
        Log.d(TAG, "onGetServiceFailure");
    }

    @Subscribe
    public void onGetCostSuccess(final IGetCostSuccessEngineEvent event){
        Log.d(TAG, "onGetCostSuccess");
        getCostObservable.postValue(event.getCost());
    }

    @Subscribe
    public void onGetCostFailure(final IGetCostFailureEngineEvent event){
        Log.d(TAG, "onGetCostFailure");
    }

    @Subscribe
    public void onGetEtaSuccess(final IGetEtaSuccessEngineEvent event){
        Log.d(TAG, "onGetEtaSuccess");
        getEtaObservable.postValue(event.getEta());
    }

    @Subscribe
    public void onGetEtaFailure(final IGetEtaFailureEngineEvent event){
        Log.d(TAG, "onGetEtaFailure");
    }

}
