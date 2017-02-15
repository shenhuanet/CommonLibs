package com.shenhua.commonlibs.mvp;

import android.text.TextUtils;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public abstract class ApiCallback<M> extends Subscriber<M> {

    public abstract void onPreExecute();

    public abstract void onSuccess(M model);

    public abstract void onFailure(String msg);

    public abstract void onFinish();

    @Override
    public void onStart() {
        super.onStart();
        onPreExecute();
    }

    @Override
    public void onCompleted() {
        onFinish();
    }

    @Override
    public void onNext(M m) {
        onSuccess(m);
    }

    @Override
    public void onError(Throwable e) {
        String msg;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            msg = httpException.getMessage();
            if (code == 504) {
                msg = "网络不给力";
            }
            if (code == 502 || code == 404) {
                msg = "服务器异常，请稍后再试";
            }
        } else {
            msg = e.getMessage();
            if (msg.contains("Unable to resolve")) {
                msg = "网络未连接";
            }
        }
        if (!TextUtils.isEmpty(msg)) {
            onFailure(msg);
        }
        onFinish();
    }
}
