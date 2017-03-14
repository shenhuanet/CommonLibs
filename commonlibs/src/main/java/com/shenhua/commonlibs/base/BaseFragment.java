package com.shenhua.commonlibs.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private Activity activity;
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
        netReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        activity = getActivity();
        if (activity != null)
            activity.registerReceiver(netReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            int mContentViewId;
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
                mToolbarId = annotation.toolbarId();
                mToolbarTitle = annotation.toolbarTitle();
                mToolbarTitleId = annotation.toolbarTitleId();
                mMenuId = annotation.menuId();
            } else {
                throw new RuntimeException("BaseFragment:Class must add annotations of ActivityFragmentInitParams.class");
            }
            rootView = inflater.inflate(mContentViewId, container, false);
            initView(rootView);
            initToolbar();
        }
        return rootView;
    }

    public abstract void initView(View rootView);

    private void initToolbar() {
        if (mToolbarId == -1) return;
        Toolbar toolbar = (Toolbar) rootView.findViewById(mToolbarId);
        if (activity != null) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ActionBar ab = getToolbar();
            assert ab != null;
            ab.setTitle("");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mMenuId != -1)
            inflater.inflate(mMenuId, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUseBusEvent)
            BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (null != parent) {
            parent.removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUseBusEvent)
            BusProvider.getInstance().unregister(this);
        if (activity != null) {
            activity.unregisterReceiver(netReceiver);
        }
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

    public void hideKeyboard() {
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
