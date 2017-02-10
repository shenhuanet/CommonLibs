package com.shenhua.commonlibs.callback;

/**
 * Http请求回调
 * Created by shenhua on 8/29/2016.
 */
public interface HttpCallback<T> {

    void onPreRequest();

    void onSuccess(T data);

    void onError(String errorInfo);

    void onPostRequest();
}
