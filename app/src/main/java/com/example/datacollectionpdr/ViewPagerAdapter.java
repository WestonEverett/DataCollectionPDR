package com.example.datacollectionpdr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Set up the fragment swipe menu used for display during recording
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>(); //list of fragments
    private final List<String> fragmentTitle = new ArrayList<>();   //list of fragments names

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    //add fragment to the list
    public void add(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitle.add(title);
    }

    //get fragment
    @NonNull @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    //check how many fragments are to be displayed in menu
    @Override
    public int getCount() {
        return fragments.size();
    }

    //get title of the fragment
    @Nullable @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}
