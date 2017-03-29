package com.shenhua.commonlibs.handler;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonUiRunnable<T> {

    private T t;

    public CommonUiRunnable(T t) {
        this.t = t;
    }

    public abstract void doUIThread();

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
