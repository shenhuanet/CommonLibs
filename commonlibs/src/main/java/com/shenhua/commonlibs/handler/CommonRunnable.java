package com.shenhua.commonlibs.handler;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonRunnable<T> {

    private T t;

    public CommonRunnable(T t) {
        this.t = t;
    }

    public abstract T doChildThread();

    public abstract void doUiThread(T t);

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
