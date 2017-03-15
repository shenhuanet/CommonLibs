package com.shenhua.commonlibs.callback;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * 通用WebView JS interface
 * <p> // @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
 * webView.addJavascriptInterface(new JSInterface(this), "imgClickListener"); // imgClickListener 不可更改
 * <p>
 * Created by Shenhua on 12/10/2016.
 * e-mail shenhuanet@126.com
 */
public class JSInterface {

    public Context context;
    private static final String TAG = "JSInterface";

    public JSInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void openImage(String url) {
        if (url != null && !TextUtils.isEmpty(url)) {
            Log.i(TAG, "openImage: imgUrl:" + url);
        }
    }
}
