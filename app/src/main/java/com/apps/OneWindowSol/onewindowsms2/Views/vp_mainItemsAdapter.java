package com.apps.OneWindowSol.onewindowsms2.Views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.apps.OneWindowSol.onewindowsms2.Group.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AbdulMueed on 12/9/2015.
 */
public class vp_mainItemsAdapter extends FragmentPagerAdapter {

    public final List<Fragment> mFragmentList = new ArrayList<>();
    public final List<String> mFragmentTitleList = new ArrayList<>();


    public vp_mainItemsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void AddFragment(Fragment frgObject, String title) {
        //here we go ..
        mFragmentList.add(frgObject);
        mFragmentTitleList.add(title);

    }
}
