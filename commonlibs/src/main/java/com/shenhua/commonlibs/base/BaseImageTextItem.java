package com.shenhua.commonlibs.base;

/**
 * 图文选择item 基类
 * Created by Shenhua on 9/18/2016.
 */
public class BaseImageTextItem {

    private String title;
    private int drawable;

    public BaseImageTextItem(int drawable, String title) {
        this.drawable = drawable;
        this.title = title;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
