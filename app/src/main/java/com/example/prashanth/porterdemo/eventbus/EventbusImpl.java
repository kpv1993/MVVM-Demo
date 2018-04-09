package com.example.prashanth.porterdemo.eventbus;

import com.example.prashanth.porterdemo.threading.BusinessExecutor;
import com.example.prashanth.porterdemo.threading.IBusinessExecutor;

import org.greenrobot.eventbus.EventBus;

public class EventbusImpl implements IEventbus {
    private static EventbusImpl sEventBusImpl = new EventbusImpl();

    private final EventBus mEventBus;
    private final IBusinessExecutor mBusinessExecutor;

    public static EventbusImpl getInstance() {
        return sEventBusImpl;
    }

    private EventbusImpl() {
        mEventBus = EventBus.builder()
                .sendNoSubscriberEvent(false)
                .throwSubscriberException(true)
                .logNoSubscriberMessages(false).build();
        mBusinessExecutor = BusinessExecutor.getInstance();
    }

    @Override
    public void register(Object subscriber) throws NullPointerException {
        mEventBus.register(subscriber);
    }

    @Override
    public void unregister(Object subscriber) throws NullPointerException {
        mEventBus.unregister(subscriber);
    }

    @Override
    public void postByBusinessThread(final Object event) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.post(event);
            }
        });
    }

    @Override
    public void post(Object event) {
        mEventBus.post(event);
    }
}