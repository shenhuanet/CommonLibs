package com.shenhua.commonlibs.base;

import android.os.AsyncTask;

/**
 * AsyncTask 基类
 * Usages:
 * <p>
 * BaseAsyncLoader&lt;String, Void, String&gt;loader = new BaseAsyncLoader();
 * loader.setOnLoadListener(this);// 实现 BaseAsyncLoader.OnLoadListener 接口
 * loader.execute(p);// 参数
 * </p>
 * Created by Shenhua on 12/6/2016.
 * e-mail shenhuanet@126.com
 */
public class BaseAsyncLoader<P, S, T> extends AsyncTask<P, S, T> {

    private OnLoadListener<P, S, T> listener;
    private Exception ex;

    public BaseAsyncLoader() {
        listener = new OnLoadListener<P, S, T>() {
            @Override
            public void onDataStart() {
            }

            @SafeVarargs
            @Override
            public final T doInWorkerThread(P... params) throws Exception {
                return null;
            }

            @Override
            public void onDataSuccess(T result) {
            }

            @Override
            public void onDataFail(Exception e) {
            }

            @Override
            public void onDataFinish() {
            }

            @SafeVarargs
            @Override
            public final void onDataProgress(S... values) {
            }
        };
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onDataStart();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T doInBackground(P... params) {
        try {
            return listener.doInWorkerThread(params);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onProgressUpdate(S... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        if (ex != null) {
            listener.onDataFail(ex);
        } else {
            listener.onDataSuccess(result);
        }
        listener.onDataFinish();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        listener.onDataFinish();
    }

    public void setOnLoadListener(OnLoadListener<P, S, T> listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public interface OnLoadListener<P, S, T> {

        void onDataStart();

        @SuppressWarnings("unchecked")
        T doInWorkerThread(P... params) throws Exception;

        @SuppressWarnings("unchecked")
        void onDataProgress(S... values);

        void onDataSuccess(T result);

        void onDataFail(Exception e);

        void onDataFinish();
    }

}
