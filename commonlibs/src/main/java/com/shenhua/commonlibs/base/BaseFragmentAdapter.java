package com.shenhua.commonlibs.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public class BaseFragmentAdapter extends FragmentPagerAdapter {

    protected List<Fragment> fragmentList;

    protected String[] mTitles;

    public BaseFragmentAdapter(FragmentManager fm) {
        this(fm, null, null);
    }

    public BaseFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] mTitles) {
        super(fm);
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        this.fragmentList = fragmentList;
        this.mTitles = mTitles;
    }

    public void add(Fragment fragment) {
        if (isEmpty()) {
            fragmentList = new ArrayList<>();
        }
        fragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return isEmpty() ? null : fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return isEmpty() ? 0 : fragmentList.size();
    }

    public boolean isEmpty() {
        return fragmentList == null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
