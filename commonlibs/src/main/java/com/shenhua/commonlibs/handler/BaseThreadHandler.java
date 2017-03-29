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
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }
            return sInstance;
        }
    }

    public static class OnUiThread<T> {

        public void onSuccess(T t) {
        }

        public void onFailed(String msg) {
        }

        public void onCanceled() {
        }
    }

    /**
     * 阻塞式Callable
     *
     * @param call     call
     * @param callback callback
     * @param <T>      t
     * @return this
     */
    public <T> BaseThreadHandler sendRunnable(Callable<T> call, OnUiThread<T> callback) {
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
        return this;
    }

    /**
     * 阻塞式Runnable
     *
     * @param runnable runnable
     * @param callback callback
     * @param <T>      t
     * @return this
     */
    public <T> BaseThreadHandler sendRunnable(Runnable runnable, OnUiThread<T> callback) {
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
        return this;
    }

    /**
     * 非阻塞式Runnable，用于子线程和主线程间，通用型
     *
     * @param t   t
     * @param <T> t
     */
    public <T> void sendRunnable(CommonRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    public <T> void send(final CommonR<T> commonR) {
        Observable.OnSubscribe<T> os = new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T result = commonR.doInBackground();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        };
        Observable.create(os).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        commonR.doInUiThread(t);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }


    /**
     * 非阻塞式Runnable，用于子线程和主线程间，通用型
     *
     * @param t        t
     * @param time     超时时间
     * @param timeUnit 超时单位
     * @param <T>      t
     */
    public <T> void sendRunnable(CommonRunnable<T> t, long time, TimeUnit timeUnit) {
        CommonSubscribe<CommonRunnable<T>> cs = new CommonSubscribe<CommonRunnable<T>>(t) {
            @Override
            public void call(Subscriber<? super CommonRunnable<T>> subscriber) {
                getC().doChildThread();
                subscriber.onNext(getC());
                subscriber.onCompleted();
            }
        };
        Observable.create(cs).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .delay(time, timeUnit)
                .subscribe(new Action1<CommonRunnable<T>>() {
                    @Override
                    public void call(CommonRunnable<T> tCommonTask) {
                        tCommonTask.doUiThread(tCommonTask.getT());
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
     * @param t   t
     * @param <T> t
     */
    public <T> void sendRunnable(CommonChildRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞式Runnable，用于子线程
     *
     * @param t        t
     * @param time     超时时间
     * @param timeUnit 超时单位
     * @param <T>      t
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
     * @param t   t
     * @param <T> t
     */
    public <T> void sendRunnable(CommonUiRunnable<T> t) {
        this.sendRunnable(t, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 非阻塞式Runnable，用于主线程
     *
     * @param t        t
     * @param time     超时时间
     * @param timeUnit 超时单位
     * @param <T>      t
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
