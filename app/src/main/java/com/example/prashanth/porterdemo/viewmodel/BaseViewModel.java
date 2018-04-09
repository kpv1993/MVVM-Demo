package com.example.prashanth.porterdemo.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import com.example.prashanth.porterdemo.eventbus.IEventbus;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

public abstract class BaseViewModel extends ViewModel implements LifecycleObserver {

    protected IEventbus eventBus;
    protected IBusinessExecutor businessExecutor;

    public BaseViewModel(IEventbus eventbus, IBusinessExecutor businessExecutor) {
        super();
        this.eventBus = eventbus;
        this.businessExecutor = businessExecutor;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        eventBus.register(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        eventBus.unregister(this);
    }
}