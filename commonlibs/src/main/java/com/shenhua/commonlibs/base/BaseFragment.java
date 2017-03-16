package com.shenhua.commonlibs.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.commonlibs.annotation.ActivityFragmentInject;
import com.shenhua.commonlibs.utils.BusProvider;

/**
 * Fragment基类
 * Created by Shenhua on 8/21/2016.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    NetworkReceiver netReceiver;
    protected View rootView;
    private boolean hasOptionsMenu;
    private int mToolbarId;
    private int mToolbarTitle;
    private int mToolbarTitleId;
    private int mMenuId;
    private boolean mUseBusEvent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            hasOptionsMenu = annotation.hasOptionsMenu();
            mUseBusEvent = annotation.useBusEvent();
        }
        setHasOptionsMenu(hasOptionsMenu);
    }

    @Override
    public void onStart() {
        super.onStart();
        netReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        Activity activity = getActivity();
        if (activity != null)
            activity.registerReceiver(netReceiver, filter);
        if (mUseBusEvent)
            BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
                int mContentViewId = annotation.contentViewId();
                mToolbarId = annotation.toolbarId();
                mToolbarTitle = annotation.toolbarTitle();
                mToolbarTitleId = annotation.toolbarTitleId();
                mMenuId = annotation.menuId();
                rootView = inflater.inflate(mContentViewId, container, false);
                onCreateView(inflater, container, savedInstanceState, rootView);
                initToolbar();
            } else {
                Log.e(TAG, "onCreateView: BaseFragment:Class must add annotations of ActivityFragmentInitParams.class", new RuntimeException());
            }
        }
        if (rootView == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        ViewGroup group = (ViewGroup) rootView.getParent();
        if (group != null)
            group.removeView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mUseBusEvent)
            BusProvider.getInstance().unregister(this);
        Activity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(netReceiver);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mMenuId != -1)
            inflater.inflate(mMenuId, menu);
    }

    public abstract void onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, View rootView);

    private void initToolbar() {
        if (mToolbarId == -1) return;
        Toolbar toolbar = (Toolbar) rootView.findViewById(mToolbarId);
        Activity activity = getActivity();
        if (activity != null) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ActionBar ab = getToolbar();
            assert ab != null;
            ab.setTitle("");
        }
        if (mToolbarTitleId != -1) {
            TextView textView = (TextView) rootView.findViewById(mToolbarTitleId);
            if (textView != null && mToolbarTitle != -1) textView.setText(mToolbarTitle);
        }
    }

    public void setupToolbarTitle(String str) {
        TextView textView = (TextView) rootView.findViewById(mToolbarTitleId);
        if (textView != null) textView.setText(str);
    }

    public void setupToolbarTitle(int resId) {
        TextView textView = (TextView) rootView.findViewById(mToolbarTitleId);
        if (textView != null) textView.setText(resId);
    }

    public void setupActionbarTitle(String str) {
        ActionBar actionBar = getToolbar();
        if (actionBar != null) {
            actionBar.setTitle(str);
        }
    }

    public void setupActionbarTitle(int resId) {
        ActionBar actionBar = getToolbar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    protected ActionBar getToolbar() {
        Activity activity = getActivity();
        return ((AppCompatActivity) activity).getSupportActionBar();
    }

    protected void showSnackBar(String msg) {
        Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void showSnackBar(@StringRes int id) {
        Snackbar.make(rootView, id, Snackbar.LENGTH_SHORT).show();
    }

    public void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {// 有网
                    onNetWorkIsOk();
                } else {
                    onNetWorkIsError();
                }
            } catch (Exception e) {
                if (e instanceof SecurityException) {
                    Log.e(TAG, "NetworkReceiver: Make sure you has defined the android.permission.ACCESS_NETWORK_STATE and ACCESS_WIFI_STATE", e);
                }
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
        Activity activity = getActivity();
        if (activity != null) {
            if (Build.VERSION.SDK_INT > 21) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                        view.findViewById(viewId), sharedElementName);
                startActivityForResult(intent, requestCode, options.toBundle());
            } else {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view,
                        view.getWidth() / 2, view.getHeight() / 2, 0, 0);
                ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle());
            }
        }
    }

    public void hideKeyboard() {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                if (activity.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }
}
