package com.example.prashanth.porterdemo.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BusinessExecutor implements IBusinessExecutor {

    private final ExecutorService mBusinessPoolExecutor;
    private final ExecutorService mResourcePoolExecutor;
    private final ExecutorService mSessionResourcePoolExecutor;
    private static BusinessExecutor mBusinessExecutor = new BusinessExecutor();
    private static final int RESOURCE_THREAD_POOL_SIZE = 15;

    public static IBusinessExecutor getInstance(){
        return mBusinessExecutor;
    }

    private BusinessExecutor() {
        mBusinessPoolExecutor = Executors.newSingleThreadExecutor();
        mResourcePoolExecutor = Executors.newFixedThreadPool(RESOURCE_THREAD_POOL_SIZE);
        mSessionResourcePoolExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void executeInBusinessThread(Runnable command) throws NullPointerException {
        mBusinessPoolExecutor.execute(command);
    }

    @Override
    public void executeInResourceThread(Runnable command) throws NullPointerException {
        mResourcePoolExecutor.execute(command);
    }

    @Override
    public Future submitInResourceThread(Runnable command) throws NullPointerException {
        return mResourcePoolExecutor.submit(command);
    }

    @Override
    public Future submitInSessionResourceThread(Runnable command) throws NullPointerException {
        return mSessionResourcePoolExecutor.submit(command);
    }

}
