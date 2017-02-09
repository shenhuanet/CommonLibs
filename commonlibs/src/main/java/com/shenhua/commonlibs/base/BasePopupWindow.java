package com.shenhua.commonlibs.base;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by Shenhua on 10/28/2016.
 * e-mail shenhuanet@126.com
 */
public abstract class BasePopupWindow extends PopupWindow {

    private Context context;
    private View mContentView;
    private boolean isShowing;

    public BasePopupWindow(Context context, @ColorInt int backgroundColor) {
        this.context = context;
        mContentView = LayoutInflater.from(context).inflate(getContentLayoutId(), null);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setContentView(mContentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(mContentView);
        ObjectAnimator.ofFloat(mContentView, "alpha", 0.5f, 1.0f).setDuration(500).start();
        ObjectAnimator.ofObject(mContentView, "backgroundColor", new ArgbEvaluator(),
                0x00000000, backgroundColor).setDuration(500).start();
    }

    @Override
    public void update() {
        super.update();
        onContentViewUpdate(context, mContentView);
    }

    public abstract void onContentViewUpdate(Context context, View contentView);

    public abstract int getContentLayoutId();

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public void showFull(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
            this.update();
            isShowing = true;
        } else {
            this.dismiss();
            isShowing = false;
        }
    }

    public void showDrop(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 2);
            this.update();
            isShowing = true;
        } else {
            this.dismiss();
            isShowing = false;
        }
    }

}
