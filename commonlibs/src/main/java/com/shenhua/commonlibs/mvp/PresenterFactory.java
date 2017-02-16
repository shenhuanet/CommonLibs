package com.shenhua.commonlibs.mvp;

/**
 * Created by shenhua on 2/16/2017.
 * Email shenhuanet@126.com
 */
public interface PresenterFactory<P extends BasePresenter> {

    P create();

}
