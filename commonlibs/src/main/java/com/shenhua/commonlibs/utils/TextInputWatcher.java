package com.shenhua.commonlibs.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.widget.EditText;

/**
 * 监听输入是否完成
 * Created by shenhua on 9/29/2016.
 */
public class TextInputWatcher implements TextWatcher {

    private OnClickableListener listener;
    private EditText[] editTexts;
    private int[] watcherLengths;
    private SparseBooleanArray sb;

    public TextInputWatcher(EditText[] editTexts, int[] watcherLengths) {
        this.editTexts = editTexts;
        this.watcherLengths = watcherLengths;
        sb = new SparseBooleanArray(editTexts.length);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        for (int i = 0; i < editTexts.length; i++) {
            if (editTexts[i].getText().length() == watcherLengths[i]) {
                sb.put(i, true);
            } else sb.put(i, false);
        }
        if (getSb(sb)) listener.onClickable();
        else listener.onNotClickable();
    }

    private boolean getSb(SparseBooleanArray sb) {
        for (int i = 0; i < sb.size(); i++) {
            if (!sb.get(i)) {// 一旦发现有false的，则返回
                return false;
            }
        }
        return true;
    }

    public void setOnClickableListener(OnClickableListener listener) {
        this.listener = listener;
    }

    public interface OnClickableListener {
        void onClickable();

        void onNotClickable();
    }
}
