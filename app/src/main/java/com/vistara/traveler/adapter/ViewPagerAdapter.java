package com.vistara.traveler.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vistara.traveler.CabFragment;
import com.vistara.traveler.HotelFragment;

/**
 * Created by Sharad on 21-06-2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new CabFragment();
        }
        else if (position == 1)
        {
            fragment = new HotelFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Cab";
        }
        else if (position == 1)
        {
            title = "Hotel";
        }
        return title;
    }
}
