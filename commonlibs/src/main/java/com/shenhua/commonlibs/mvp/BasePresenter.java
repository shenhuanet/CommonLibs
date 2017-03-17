package com.shenhua.commonlibs.mvp;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public class BasePresenter<V extends BaseView> {

    protected V mvpView;
    private CompositeSubscription mCompositeSubscription;

    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    public void detachView() {
        this.mvpView = null;
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    /**
     * To determine whether the view is empty
     *
     * @return false is not null
     */
    public boolean isAttachView() {
        return mvpView != null;
    }

    /**
     * return target view
     *
     * @return mvpView
     */
    public V getMvpView() {
        return mvpView;
    }

    /**
     * Check whether the view and presenter are attached
     */
    public void checkViewAttach() {
        if (!isAttachView()) {
            throw new MvpViewNotAttachedException();
        }
    }

    /**
     * Custom exception
     */
    public static class MvpViewNotAttachedException extends RuntimeException {
        MvpViewNotAttachedException() {
            super("Please use the attachView (MvpView) method to establish a connection with View before calling");
        }
    }

    /**
     * Add http request subscription
     *
     * @param observable HttpManager.getInstance().createHtmlGetObservable()
     * @param subscriber new ApiCallback()  run callback's methods on mainThread.
     */
    @SuppressWarnings("unchecked")
    public void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

}
