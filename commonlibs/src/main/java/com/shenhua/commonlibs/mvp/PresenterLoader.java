package com.shenhua.commonlibs.mvp;

import android.content.Context;
import android.support.v4.content.Loader;

/**
 * Created by shenhua on 2/16/2017.
 * Email shenhuanet@126.com
 */
public class PresenterLoader<P extends BasePresenter> extends Loader<P> {

    private final PresenterFactory<P> factory;
    private P presenter;

    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     * @param factory factory
     */
    public PresenterLoader(Context context, PresenterFactory<P> factory) {
        super(context);
        this.factory = factory;
    }

    /**
     * 在Activity的onStart()调用之后
     */
    @Override
    protected void onStartLoading() {
        if (presenter != null) {
            deliverResult(presenter);
            return;
        }
        forceLoad();
    }

    /**
     * 在调用forceLoad()方法后自动调用，在这个方法中创建Presenter并返回它。
     */
    @Override
    protected void onForceLoad() {
        presenter = factory.create();
        deliverResult(presenter);
    }

    /**
     * 会在Loader被销毁之前调用，可以在这里告知Presenter以终止某些操作或进行清理工作。
     */
    @Override
    protected void onReset() {
        presenter = null;
    }
}
