package com.shenhua.shenhua_commonlib;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.shenhua.commonlibs.annotation.ActivityFragmentInject;
import com.shenhua.commonlibs.base.BaseActivity;
import com.shenhua.shenhua_commonlib.databinding.ActivityMainBinding;

@ActivityFragmentInject(
        toolbarId = R.id.common_toolbar,
        toolbarTitle = R.string.app_name,
        toolbarTitleId = R.id.tv_title,
        toolbarHomeAsUp = true
)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(BaseActivity baseActivity, Bundle savedInstanceState) {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initToolbar();
    }

}
