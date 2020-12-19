package com.example.aptapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class GroupsFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    ArrayList<ArrayList<String>> groupsByDepartaments;
    private final String[] tabTitles;
    private final int courseNumber;

    public GroupsFragmentPagerAdapter(FragmentManager fm, int courseNumber, String[] tabTitles){
        super(fm);
        // получение необходимых данных
        this.courseNumber = courseNumber;
        this.tabTitles = tabTitles;
        this.groupsByDepartaments = groupsByDepartaments;
    }

    // метод для вывода данных на каждой вкладке
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return GroupsViewFragment.newInstance(position, courseNumber);

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    // выставление заголовков вкладок
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
