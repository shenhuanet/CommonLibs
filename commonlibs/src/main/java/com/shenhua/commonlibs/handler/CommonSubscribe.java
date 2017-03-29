package com.shenhua.commonlibs.handler;

import rx.Observable;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonSubscribe<C> implements Observable.OnSubscribe<C> {

    private C c;

    public CommonSubscribe(C c) {
        this.c = c;
    }

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }
}
