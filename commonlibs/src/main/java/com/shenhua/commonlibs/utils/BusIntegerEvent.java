package com.shenhua.commonlibs.utils;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class BusIntegerEvent {

    private int anInt;

    public BusIntegerEvent(int anInt) {
        this.anInt = anInt;
    }

    public int getInt() {
        return anInt;
    }

    public void setInt(int anInt) {
        this.anInt = anInt;
    }

    @Override
    public String toString() {
        return "BusIntegerEvent:" + anInt;
    }
}
