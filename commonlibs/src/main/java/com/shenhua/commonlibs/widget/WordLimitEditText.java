package com.shenhua.commonlibs.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shenhua.libs.common.R;

/**
 * 一个可以自动检测输入字符长度并限制的EditText
 * Created by shenhua on 8/5/2016.
 */
public class WordLimitEditText extends FrameLayout implements TextWatcher {

    private String mCurrentCount;
    private FrameLayout frameLayout;
    private EditText editText;
    private TextView textView;

    private void init() {
        inflate(getContext(), R.layout.common_view_limit_edittext, this);
        frameLayout = (FrameLayout) findViewById(R.id.edit_frame);
        editText = (EditText) findViewById(R.id.ev_content);
        textView = (TextView) findViewById(R.id.tv_num);
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int num = editText.getText().length();
        if (mCurrentCount == null) {
            textView.setText(String.format("%d/0", num));
            return;
        }
        textView.setText(String.format(mCurrentCount, num));
    }

    public WordLimitEditText(Context context) {
        super(context);
        init();
    }

    public WordLimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordLimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFrameLayoutHeight(int height) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        frameLayout.setLayoutParams(params);
    }

    public void setMaxLengh(int lengh) {
        mCurrentCount = "%d/" + String.valueOf(lengh);
        textView.setText(String.format(mCurrentCount, 0));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lengh)});
    }

    public void setEditBackgroundResource(int resid) {
        editText.setBackgroundResource(resid);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String str) {
        editText.setText(str);
    }
}
