package com.shenhua.commonlibs.callback;

/**
 * 6.0权限请求坚挺接口
 * Created by shenhua on 3/15/2017.
 * Email shenhuanet@126.com
 */
public interface PermissionsResultListener {

    void onPermissionGranted();

    void onPermissionDenied();

}
