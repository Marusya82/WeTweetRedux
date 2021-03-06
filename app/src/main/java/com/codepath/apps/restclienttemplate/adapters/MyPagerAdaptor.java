package com.codepath.apps.restclienttemplate.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MessagesFragment;

public class MyPagerAdaptor extends SmartFragmentStatePagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Home", "Mentions", "Messages" };

    public MyPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeTimelineFragment.newInstance(0, "");
            case 1:
                return MentionsTimelineFragment.newInstance(1, "");
            case 2:
                return MessagesFragment.newInstance(2, "");
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}