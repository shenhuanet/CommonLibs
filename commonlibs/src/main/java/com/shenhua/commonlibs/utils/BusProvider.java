package com.shenhua.commonlibs.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class BusProvider extends Bus {

    private static BusProvider sBus = new BusProvider();// 单例模式
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static BusProvider getInstance() {
        return sBus;
    }

    private BusProvider() {
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BusProvider.super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    BusProvider.super.post(event);
                }
            });
        }
    }
}
