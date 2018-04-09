package com.example.prashanth.porterdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.prashanth.porterdemo.eventbus.EventbusImpl;
import com.example.prashanth.porterdemo.eventbus.IEventbus;
import com.example.prashanth.porterdemo.events.IGetServiceSuccessEngineEvent;
import com.example.prashanth.porterdemo.factory.EngineLifeCycleController;
import com.example.prashanth.porterdemo.threading.BusinessExecutor;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

public class AppController extends Application {

    public static final String TAG = "AppController";
    private static Set<Activity> sActivitiesInForeground = new HashSet<Activity>();
    private static Set<Activity> sActivitiesLaunched = new HashSet<Activity>();
    private static AppController mInstance;
    private static Context sContext;
    private EngineLifeCycleController engineLifeCycleController;
    private IBusinessExecutor businessExecutor;
    private IEventbus eventbus;

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public static Context getContext(){
        return sContext;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Oncreate");
        mInstance = this;
        sContext = this;
        registerActivityLifecycleCallbacks(new ActivityLifeCycleCallBackImpl());
        super.onCreate();
        eventbus = EventbusImpl.getInstance();
        eventbus.register(this);
        engineLifeCycleController = new EngineLifeCycleController();
        businessExecutor = BusinessExecutor.getInstance();
        businessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                engineLifeCycleController.init();

            }
        });
        Log.d(TAG, "Oncreate");
    }

    private class ActivityLifeCycleCallBackImpl implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated " + activity.getLocalClassName());
            sActivitiesLaunched.add(activity);

        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted " + activity.getClass().getSimpleName());
            sActivitiesInForeground.add(activity);

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed " + activity.getLocalClassName());

        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused " + activity.getLocalClassName());

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped " + activity.getLocalClassName());
            sActivitiesInForeground.remove(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState " + activity.getLocalClassName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed " + activity.getLocalClassName());
            sActivitiesLaunched.remove(activity);
        }
    }

    @Subscribe
    public void dummyEvent(IGetServiceSuccessEngineEvent event) {}

}
