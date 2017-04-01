package com.shenhua.commonlibs.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shenhua on 4/1/2017.
 * Email shenhuanet@126.com
 */
public class QueryString {

    private StringBuilder query = new StringBuilder();

    public synchronized QueryString add(String name, String value) {
        query.append("&");
        if (name == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (value == null) {
            value = "";
        }
        query.append(encoderString(name));
        query.append("=");
        query.append(value.length() == 0 ? value : encoderString(value));
        return this;
    }

    public String encoderString(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }

    @Override
    public synchronized String toString() {
        if (query.length() > 0) {
            query.replace(0, 1, "?");
            return query.toString();
        }
        return query.toString();
    }

}
