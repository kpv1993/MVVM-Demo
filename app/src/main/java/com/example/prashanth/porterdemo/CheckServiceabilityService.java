package com.example.prashanth.porterdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.prashanth.porterdemo.engine.Engine;
import com.example.prashanth.porterdemo.rest.ServerAddress;

public class CheckServiceabilityService extends Service {

    public static int SERVICE_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Engine.getInstance().getService();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
