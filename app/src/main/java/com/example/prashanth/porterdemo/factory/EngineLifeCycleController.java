package com.example.prashanth.porterdemo.factory;

import android.util.Log;

import com.example.prashanth.porterdemo.engine.Engine;
import com.example.prashanth.porterdemo.engine.IEngine;
import com.example.prashanth.porterdemo.eventbus.EventbusImpl;
import com.example.prashanth.porterdemo.eventbus.IEventbus;

import java.util.ArrayList;
import java.util.List;

public class EngineLifeCycleController {

    private static final String TAG = "EngineLifeCycleControl";
    private List<IEngine> mEngines = new ArrayList<IEngine>();
    private final IEventbus mEventBus;
    private String accessToken;
    private boolean isAuthenticated;

    public EngineLifeCycleController() {
        mEventBus = EventbusImpl.getInstance();
    }

    public void init() {
        Log.d(TAG, "init");

        //initiate business modules
        //ADD ALL MODULES HERE
        mEngines = new ArrayList<IEngine>();
        mEngines.add(Engine.getInstance());
        for (IEngine manager : mEngines) {
            manager.onStart();
        }
    }
}

