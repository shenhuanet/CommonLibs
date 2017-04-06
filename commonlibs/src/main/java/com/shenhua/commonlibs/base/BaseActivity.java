package com.shenhua.commonlibs.base;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shenhua.commonlibs.annotation.ActivityFragmentInject;
import com.shenhua.commonlibs.callback.PermissionsResultListener;
import com.shenhua.commonlibs.utils.BusProvider;
import com.shenhua.libs.common.R;

/**
 * Activity基类
 * Created by Shenhua on 8/21/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private NetworkReceiver netReceiver;
    private int mToolbarId;
    private boolean mToolbarHomeAsUp;
    private int mToolbarTitle;
    private int mToolbarTitleId;
    private int mMenuId;
    private boolean mUseBusEvent;
    private PermissionsResultListener mPermissionsResultListener;
    private int mRequestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            int mContentViewId = annotation.contentViewId();
            mToolbarId = annotation.toolbarId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarTitleId = annotation.toolbarTitleId();
            mToolbarHomeAsUp = annotation.toolbarHomeAsUp();
            mMenuId = annotation.menuId();
            mUseBusEvent = annotation.useBusEvent();
            if (mContentViewId != -1) {
                setContentView(mContentViewId);
            }
            initToolbar();
            onCreate(this, savedInstanceState);
        } else {
            Log.e(TAG, "onCreate: BaseActivity:Class must add annotations of ActivityFragmentInitParams.class", new RuntimeException());
        }
        netReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
        if (mUseBusEvent)
            BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netReceiver);
        if (mUseBusEvent)
            BusProvider.getInstance().unregister(this);
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

    protected abstract void onCreate(BaseActivity baseActivity, Bundle savedInstanceState);

    /**
     * 调用 performRequestPermissions 方法来权限请求
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    protected void performRequestPermissions(String desc, String[] permissions, int requestCode, PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mPermissionsResultListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mPermissionsResultListener != null) {
                    mPermissionsResultListener.onPermissionGranted();
                }
            }
        } else {
            if (mPermissionsResultListener != null) {
                mPermissionsResultListener.onPermissionGranted();
            }
        }
    }

    /**
     * 使状态栏透明
     */
    public void initFitsWindow() {
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

    public void initToolbar() {
        if (mToolbarId == -1) return;// 无toolbar
        Toolbar toolbar = (Toolbar) findViewById(mToolbarId);
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        ActionBar ab = getToolbar();
        if (ab == null) return;
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

    public ActionBar getToolbar() {
        return getSupportActionBar();
    }

    public View getDecorView() {
        return getWindow().getDecorView();
    }

    public void showSnackBar(String msg) {
        Snackbar.make(getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBar(@StringRes int id) {
        Snackbar.make(getDecorView(), id, Snackbar.LENGTH_SHORT).show();
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
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

    /**
     * 检察每个权限是否申请
     *
     * @param permissions permissions
     * @return true 需要申请权限， false 已申请权限
     */
    private boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param desc        描述
     * @param permissions permissions
     * @param requestCode requestCode
     */
    private void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(desc, permissions, requestCode);
        } else {
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
        }
    }

    private void showRationaleDialog(String desc, final String[] permissions, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.commong_tips))
                .setMessage(desc)
                .setPositiveButton(getResources().getString(R.string.commong_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.commong_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions permissions
     * @return true 需要再次声明，false 不需要再次声明
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查回调结果
     *
     * @param grantResults grantResults
     * @return false 回调失败，true 回调成功
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (checkEachPermissionsGranted(grantResults)) {
                if (mPermissionsResultListener != null) {
                    mPermissionsResultListener.onPermissionGranted();
                }
            } else {// 用户拒绝申请权限
                if (mPermissionsResultListener != null) {
                    mPermissionsResultListener.onPermissionDenied();
                }
            }
        }
    }

}
