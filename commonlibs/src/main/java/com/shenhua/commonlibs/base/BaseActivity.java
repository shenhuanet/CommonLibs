package com.shenhua.commonlibs.base;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.commonlibs.annotation.ActivityFragmentInject;
import com.shenhua.commonlibs.utils.BusProvider;
import com.shenhua.libs.common.R;

/**
 * Activity基类
 * Created by Shenhua on 8/21/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    NetworkReceiver netReceiver;
    private int mToolbarId;
    private boolean mToolbarHomeAsUp;
    private int mToolbarTitle;
    private int mToolbarTitleId;
    private int mMenuId;
    private boolean mUseBusEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mContentViewId;
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mToolbarId = annotation.toolbarId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarTitleId = annotation.toolbarTitleId();
            mToolbarHomeAsUp = annotation.toolbarHomeAsUp();
            mMenuId = annotation.menuId();
            mUseBusEvent = annotation.useBusEvent();
        } else {
            throw new RuntimeException("BaseActivity:Class must add annotations of ActivityFragmentInitParams.class");
        }
        setContentView(mContentViewId);
        initToolbar();
        initView(this);
        netReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
        if (mUseBusEvent)
            BusProvider.getInstance().register(this);
    }

    protected abstract void initView(BaseActivity baseActivity);

    /**
     * 使状态栏透明
     */
    private void initFitsWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void initToolbar() {
        if (mToolbarId == -1) return;// 无toolbar
        Toolbar toolbar = (Toolbar) findViewById(mToolbarId);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        ActionBar ab = getToolbar();
        assert ab != null;
        ab.setTitle("");
        if (mToolbarHomeAsUp) {
            ab.setHomeAsUpIndicator(R.drawable.ic_back_white);
            ab.setDisplayHomeAsUpEnabled(mToolbarHomeAsUp);
            ab.setDisplayShowHomeEnabled(mToolbarHomeAsUp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT > 21)
                        finishAfterTransition();
                    else
                        finish();
                }
            });
        }
        if (mToolbarTitleId != -1) {
            TextView textView = (TextView) findViewById(mToolbarTitleId);
            if (textView != null && mToolbarTitle != -1) textView.setText(mToolbarTitle);
        }
    }

    public void setupToolbarTitle(String str) {
        TextView textView = (TextView) findViewById(mToolbarTitleId);
        if (textView != null) textView.setText(str);
    }

    public void setupToolbarTitle(int resId) {
        TextView textView = (TextView) findViewById(mToolbarTitleId);
        if (textView != null) textView.setText(resId);
    }

    public void setupActionbarTitle(String str) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(str);
        }
    }

    public void setupActionbarTitle(int resId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    protected ActionBar getToolbar() {
        return getSupportActionBar();
    }

    protected View getDecorView() {
        return getWindow().getDecorView();
    }

    protected void showSnackBar(String msg) {
        Snackbar.make(getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackBar(@StringRes int id) {
        Snackbar.make(getDecorView(), id, Snackbar.LENGTH_SHORT).show();
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenuId != -1) {
            getMenuInflater().inflate(mMenuId, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (Build.VERSION.SDK_INT > 21)
                finishAfterTransition();
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netReceiver);
        if (mUseBusEvent)
            BusProvider.getInstance().unregister(this);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {// 有网
                onNetWorkIsOk();
            } else {
                onNetWorkIsError();
            }
        }
    }

    protected void onNetWorkIsOk() {

    }

    protected void onNetWorkIsError() {

    }

    /**
     * Activity场景切换动画
     *
     * @param intent            intent
     * @param requestCode       requestCode
     * @param view              需要场景变换的控件的父布局
     * @param viewId            需要场景变换的控件的id
     * @param sharedElementName 接受场景变换的控件的sharedElementName，需要在布局文件中指定
     */
    public void sceneTransitionTo(Intent intent, int requestCode, View view, int viewId, String sharedElementName) {
        if (Build.VERSION.SDK_INT > 21) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    view.findViewById(viewId), sharedElementName);
            startActivityForResult(intent, requestCode, options.toBundle());
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view,
                    view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivityForResult(this, intent, requestCode, options.toBundle());
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
