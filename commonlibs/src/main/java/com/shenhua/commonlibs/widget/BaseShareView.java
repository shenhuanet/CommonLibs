package com.shenhua.commonlibs.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.shenhua.libs.common.R;

/**
 * ShareView 基类
 * 需要在引用的xml中 声明 app:contentLayout="@layout/you layout"，you layout 高度最好设置为 wrap_content
 * Created by Shenhua on 9/2/2016.
 */
public class BaseShareView extends FrameLayout implements View.OnClickListener {

    private static final int DURATION_DEFAULT = 500;// 显示或隐藏时的时间
    private Context mContext;// 上下文
    private View mMaskView;// 暗灰色背景
    private View mContentView;// popView真正的界面
    private boolean isShowing;// 界面是否显示
    private int duration;// 时间
    private int contentHeight;// ContentView高度，用于ObjectAnimator
    private Interpolator interpolator;// 值插值器

    public BaseShareView(Context context) {
        this(context, null);
    }

    public BaseShareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PopContentView, defStyleAttr, 0);
        int layout = a.getResourceId(R.styleable.PopContentView_contentLayout, -1);
        duration = DURATION_DEFAULT;
        a.recycle();
        mMaskView = new View(mContext);
        mMaskView.setOnClickListener(this);
        mMaskView.setBackgroundColor(0x8f000000);
        mMaskView.setVisibility(GONE);
        LayoutParams maskParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mMaskView, maskParams);
        if (layout == -1) {
            throw new NullPointerException("BasePopView:you must statement a contentView at xml inside BasePopView");
        }
        mContentView = LayoutInflater.from(mContext).inflate(layout, this, false);
        mContentView.setVisibility(GONE);
        mContentView.setOnClickListener(this);
        LayoutParams contentPrams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentPrams.setMargins(5, 5, 5, 5);
        contentPrams.gravity = Gravity.BOTTOM;
        this.bringChildToFront(mContentView);
        addView(mContentView, contentPrams);
        interpolator = new LinearInterpolator();
    }

    @Override
    public void onClick(View v) {
        if (v == mMaskView || v == mContentView)
            if (isShowing) hide();
    }

    /**
     * popView开始显示，开始动画开始
     */
    public void show() {
        isShowing = true;
        mMaskView.setVisibility(VISIBLE);
        mContentView.setVisibility(VISIBLE);
        ObjectAnimator.ofFloat(mMaskView, "alpha", 0f, 1f).setDuration(duration).start();
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                contentHeight = mContentView.getHeight();
                ObjectAnimator contentViewShowAnim = ObjectAnimator.ofFloat(mContentView, "translationY", contentHeight, 0);
                contentViewShowAnim.setDuration(duration);
                contentViewShowAnim.setInterpolator(interpolator);
                contentViewShowAnim.start();
            }
        });
    }

    /**
     * popView开始消失，消失动画开始
     */
    public void hide() {
        isShowing = false;
        ObjectAnimator maskViewHideAnim = ObjectAnimator.ofFloat(mMaskView, "alpha", 1f, 0f);
        maskViewHideAnim.setDuration(duration);
        maskViewHideAnim.start();
        maskViewHideAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMaskView.setVisibility(GONE);
                mContentView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ObjectAnimator contentViewHideAnim = ObjectAnimator.ofFloat(mContentView, "translationY", 0, contentHeight);
        contentViewHideAnim.setDuration(duration);
        contentViewHideAnim.setInterpolator(interpolator);
        contentViewHideAnim.start();
    }

    /**
     * 获取popView是否显示
     *
     * @return true显示反之未显示
     */
    public boolean getIsShowing() {
        return isShowing;
    }

    /**
     * 获取到当前的contentView
     *
     * @return mContentView
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * 获取到当前的MaskView
     *
     * @return mMaskView
     */
    public View getMaskView() {
        return mMaskView;
    }

    /**
     * 设置动画时间
     *
     * @param duration 时间
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置动画时间插值器
     *
     * @param interpolator 插值器
     */
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    /**
     * 设置背景半透明颜色
     *
     * @param color
     */
    public void setMaskViewBackgroundColor(int color) {
        mMaskView.setBackgroundColor(color);
    }
}
