package com.shenhua.commonlibs.utils;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class BusBooleanEvent {

    private boolean mBoolean;

    public BusBooleanEvent(boolean mBoolean) {
        this.mBoolean = mBoolean;
    }

    public boolean isBoolean() {
        return mBoolean;
    }

    public void setBoolean(boolean mBoolean) {
        this.mBoolean = mBoolean;
    }

    @Override
    public String toString() {
        return "BusBooleanEvent:" + mBoolean;
    }
}
