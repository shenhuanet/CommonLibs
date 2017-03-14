package com.shenhua.commonlibs.utils;

import com.squareup.otto.Bus;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class BusProvider {

    private static Bus sBus;

    public static Bus getInstance() {
        if (sBus == null) {
            sBus = new Bus();
        }
        return sBus;
    }
}
