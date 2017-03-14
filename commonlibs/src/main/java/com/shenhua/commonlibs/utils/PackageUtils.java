package com.shenhua.commonlibs.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class PackageUtils {

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
