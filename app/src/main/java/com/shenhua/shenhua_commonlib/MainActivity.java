package com.shenhua.shenhua_commonlib;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shenhua.commonlibs.annotation.ActivityFragmentInject;
import com.shenhua.commonlibs.base.BaseActivity;
import com.shenhua.commonlibs.mvp.ApiCallback;
import com.shenhua.commonlibs.mvp.BasePresenter;
import com.shenhua.commonlibs.mvp.HttpManager;

@ActivityFragmentInject(
        contentViewId = R.layout.activity_main,
        toolbarId = R.id.common_toolbar,
        toolbarHomeAsUp = true,
        toolbarTitle = R.string.app_name,
        toolbarTitleId = R.id.tv_title
)
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(BaseActivity baseActivity, Bundle savedInstanceState) {
        setupToolbarTitle(R.string.app_name);
    }

    public void request(View view) {
        BasePresenter pre = new BasePresenter();
        pre.addSubscription(HttpManager.getInstance().createHtmlGetObservable(this, "https://github.com/"), new ApiCallback<String>() {
            @Override
            public void onPreExecute() {
                Log.d(TAG, "onPreExecute: ");
            }

            @Override
            public void onSuccess(String str) {
                Log.d(TAG, "onSuccess: " + str);
            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onFinish() {
            }
        });
    }

    public void read(View view) {
    }
}
