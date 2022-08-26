package com.example.mdp_android_grp01.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterController extends FragmentPagerAdapter {

    private final List<Fragment> fragmentsArray =  new ArrayList<>();
    private final List<String> fragmentsTitle =  new ArrayList<>();

    public SectionsPagerAdapterController(Context context, FragmentManager fm) {
        super(fm);
    }
    public void addFragment (Fragment fragment, String Title){
        fragmentsArray.add(fragment);
        fragmentsTitle.add(Title);

    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentsArray.get(position);

    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentsTitle.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsArray.size();
    }

}