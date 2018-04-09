package com.example.prashanth.porterdemo.eventbus;

public interface IEventbus {
    /**
     * @see EventbusImpl#register(Object)
     */
    public void register(Object subscriber) throws NullPointerException;

    /**
     * @see EventbusImpl#unregister(Object)
     */
    public void unregister(Object subscriber) throws NullPointerException;

    void postByBusinessThread(Object event);

    /**
     * @see EventbusImpl#post(Object)
     */
    public void post(Object event);
}