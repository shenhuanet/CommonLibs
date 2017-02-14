package com.shenhua.commonlibs.callback;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * 编辑框变化监听器，主要用来清除TextInputLayout的error信息
 * Created by Shenhua on 9/29/2016.
 */
public class TextEnableInputWatcher implements TextWatcher {

    private TextInputLayout textInputLayout;

    public TextEnableInputWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textInputLayout.setErrorEnabled(false);
    }
}
