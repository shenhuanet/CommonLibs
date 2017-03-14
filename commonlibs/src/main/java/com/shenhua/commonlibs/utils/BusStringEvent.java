package com.shenhua.commonlibs.utils;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class BusStringEvent {

    private String str;

    public BusStringEvent(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "BusStringEvent:" + str;
    }
}
