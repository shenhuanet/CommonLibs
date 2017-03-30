package com.shenhua.commonlibs.handler;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by shenhua on 3/29/2017.
 * Email shenhuanet@126.com
 */
public class BaseThreadHandler {

    private static final String TAG = "BaseThreadHandler";
    private static BaseThreadHandler sInstance = null;
    private static ExecutorService executorService = null;
    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private static final int CANCELED = -1;
    private Callback mCallback;

    public static BaseThreadHandler getInstance() {
        synchronized (BaseThreadHandler.class) {
            if (sInstance == null) {
                sInstance = new BaseThreadHandler();
            }
            return sInstance;
        }
    }

    /**
     * 阻塞式Callable
     *
     * @param call     Callable call
     * @param callback OnUiThread callback
     * @param <T>      数据类型
     */
    public <T> void sendRunnable(Callable<T> call, OnUiThread<T> callback) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        if (call != null) {
            mCallback = new Callback<>(callback);
            try {
                Future<T> future = executorService.submit(call);
                T result = future.get();
                if (mCallback != null) {
                    mCallback.obtainMessage(SUCCESS, result).sendToTarget();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (mCallback != null) {
                    mCallback.obtainMessage(CANCELED).sendToTarget();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
                if (mCallback != null) {
                    mCallback.obtainMessage(FAILED, "执行时遇到了错误").sendToTarget();
                }
            }
        }
    }

    /**
     * 阻塞式Runnable
     *
     * @param runnable runnable
     * @param callback OnUiThread callback
     * @param <T>      数据类型
     */
    public <T> void sendRunnable(Runnable runnable, OnUiThread<T> callback) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        if (runnable != null) {
            mCallback = new Callback<>(callback);
            try {
                Future future = executorService.submit(runnable);
                future.get();
                if (mCallback != null) {
                    mCallback.obtainMessage(SUCCESS).sendToTarget();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (mCallback != null) {
                    mCallback.obtainMessage(CANCELED).sendToTarget();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
                if (mCallback != null) {
                    mCallback.obtainMessage(FAILED, "执行时遇到了错误").sendToTarget();
                }
            }
        }
    }

    /**
     * 非阻塞式Runnable，用于子线程和主线程间，通用型
     *
     * @param t   CommonRunnable
     * @param <T> 数据类型
     */
    public <T> void sendRunnable(CommonRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞式Runnable，用于子线程和主线程间，通用型
     *
     * @param ccr      CommonRunnable
     * @param time     延迟一段时间再执行
     * @param timeUnit 延时单位
     * @param <T>      数据类型
     */
    public <T> void sendRunnable(final CommonRunnable<T> ccr, long time, TimeUnit timeUnit) {
        Observable.OnSubscribe<T> os = new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T result = ccr.doChildThread();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        };
        Observable.create(os).delay(time, timeUnit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        ccr.doUiThread(t);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 非阻塞式Runnable，用于子线程
     *
     * @param t   CommonChildRunnable
     * @param <T> 数据类型
     */
    public <T> void sendRunnable(CommonChildRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞式Runnable，用于子线程
     *
     * @param t        CommonChildRunnable
     * @param time     延迟一段时间再执行
     * @param timeUnit 延时单位
     * @param <T>      数据类型
     */
    public <T> void sendRunnable(final CommonChildRunnable<T> t, long time, TimeUnit timeUnit) {
        Observable.just(t).delay(time, timeUnit).observeOn(Schedulers.io())
                .subscribe(new Action1<CommonChildRunnable<T>>() {
                    @Override
                    public void call(CommonChildRunnable<T> tCommonChildRunnable) {
                        t.doChildThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 非阻塞式Runnable，用于主线程
     *
     * @param t   CommonUiRunnable
     * @param <T> 数据类型
     */
    public <T> void sendRunnable(CommonUiRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞式Runnable，用于主线程
     *
     * @param t        CommonUiRunnable
     * @param time     延迟一段时间再执行
     * @param timeUnit 延时单位
     * @param <T>      数据类型
     */
    public <T> void sendRunnable(final CommonUiRunnable<T> t, long time, TimeUnit timeUnit) {
        Observable.just(t).delay(time, timeUnit).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CommonUiRunnable<T>>() {
                    @Override
                    public void call(CommonUiRunnable<T> tCommonUiRunnable) {
                        t.doUIThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public static class OnUiThread<T> {

        public void onSuccess(T t) {
        }

        public void onFailed(String msg) {
        }

        public void onCanceled() {
        }
    }

    private static class Callback<T> extends Handler {

        OnUiThread<T> callback;

        public Callback(OnUiThread<T> callback) {
            this.callback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    callback.onSuccess((T) msg.obj);
                    break;
                case FAILED:
                    callback.onFailed((String) msg.obj);
                    break;
                case CANCELED:
                    callback.onCanceled();
                    break;
            }

        }
    }

}
