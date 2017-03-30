package com.shenhua.commonlibs.handler;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonChildRunnable<T> {

    private T t;

    public CommonChildRunnable(T t) {
        this.t = t;
    }

    public abstract void doChildThread();

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
