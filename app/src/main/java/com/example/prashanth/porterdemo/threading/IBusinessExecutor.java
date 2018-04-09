package com.example.prashanth.porterdemo.threading;

import java.util.concurrent.Future;

public interface IBusinessExecutor {

    /**
     * Creates one-shot action and executes it ASAP on business thread.
     * Passed command may have to wait for other tasks to complete before
     * running.
     * @pre {@code null != command}
     * @param command command to run on business thread
     * @throws NullPointerException if passed command is {@code null}
     */
    public void executeInBusinessThread(Runnable command) throws NullPointerException;

    /**
     * Executes tasks on Resource thread pool. Pool Size = 15
     * Intended only for content provider, file, database operations within app
     * @param command
     * @throws NullPointerException
     */
    public void executeInResourceThread(Runnable command) throws NullPointerException;

    Future submitInResourceThread(Runnable command) throws NullPointerException;

    Future submitInSessionResourceThread(Runnable command) throws NullPointerException;
}
