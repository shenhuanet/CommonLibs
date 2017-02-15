package com.shenhua.commonlibs.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.shenhua.libs.common.R;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public class SampleItemAnimator extends BaseItemAnimator {

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        View icon = holder.itemView.findViewById(R.id.icon);
        icon.setRotationX(30);
        View right = holder.itemView.findViewById(R.id.right);
        right.setPivotX(0);
        right.setPivotY(0);
        right.setRotationY(90);
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        View target = holder.itemView;
        View icon = target.findViewById(R.id.icon);
        Animator swing = ObjectAnimator.ofFloat(icon, "rotationX", 45, 0);
        swing.setInterpolator(new OvershootInterpolator(5));

        View right = holder.itemView.findViewById(R.id.right);
        Animator rotateIn = ObjectAnimator.ofFloat(right, "rotationY", 90, 0);
        rotateIn.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(getAddDuration());
        animator.playTogether(swing, rotateIn);

        animator.start();
    }

    @Override
    public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override
    public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }
}
