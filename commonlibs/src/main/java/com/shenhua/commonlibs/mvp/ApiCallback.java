package com.shenhua.commonlibs.mvp;

import com.shenhua.commonlibs.utils.HttpRequestException;

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
        if (e instanceof HttpRequestException) {
            HttpRequestException httpRequestException = (HttpRequestException) e;
            onFailure(httpRequestException.getMsg());
        }
        if (e instanceof HttpException) {
            if (e.getMessage().contains("Unable to resolve")) {
                msg = "网络未连接";
                onFailure(msg);
            }
        }
        onFinish();
    }
}
