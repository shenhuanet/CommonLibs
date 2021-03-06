package com.shenhua.commonlibs.base;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebViewClient 基类
 * <p>
 * webView.setWebViewClient(new BaseWebViewClient());
 * <p>
 * Created by Shenhua on 12/9/2016.
 * e-mail shenhuanet@126.com
 */
public class BaseWebViewClient extends WebViewClient {

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.loadUrl("javascript:(function () {" +
                "    var objs = document.getElementsByTagName(\"img\");" +
                "    for (var i = 0; i < objs.length; i++) {" +
                "        objs[i].onclick = function () {" +
                "            window.imgClickListener.openImage(this.src);" +
                "        }" +
                "    }" +
                "})()");
    }
}
