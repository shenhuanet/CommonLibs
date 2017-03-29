package com.shenhua.commonlibs.handler;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonRunnable<T> {

    public abstract T doChildThread();

    public abstract void doUiThread(T t);
}
