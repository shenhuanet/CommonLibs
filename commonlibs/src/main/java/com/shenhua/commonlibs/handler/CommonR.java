package com.shenhua.commonlibs.handler;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public abstract class CommonR<T> {

    public abstract T doInBackground();

    public abstract void doInUiThread(T t);
}
