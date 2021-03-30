package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.TypeAdapterFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class SelectUserTypeActivity extends ThemedActivity {

    TabLayout userTypeTabLayout;
    ViewPager fragmentChoose;

    SharedPreferences scheduleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        scheduleData = getSharedPreferences("ScheduleData", Context.MODE_PRIVATE);
        String userType = scheduleData.getString("userType", null);
        String id = scheduleData.getString("id", null);
        String name = scheduleData.getString("name", null);

        // если до этого открывалось расписание это не перезагрузка активити при перевороте телефона перекидываем на ту же группу
        if (savedInstanceState == null) {
            Intent intent = new Intent(this, ScheduleActivity.class);

            if (userType != null && id != null && name != null) {
                intent.putExtra("user_type", UserType.valueOf(userType));
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        }

        fragmentChoose = findViewById(R.id.fragmentChoose);
        setupViewPager(fragmentChoose);

        userTypeTabLayout = findViewById(R.id.userTypeTabLayout);
        userTypeTabLayout.setupWithViewPager(fragmentChoose);
    }

    private void setupViewPager(ViewPager viewPager) {
        ChooseUserTypeViewPagerAdapter adapter = new ChooseUserTypeViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SelectCourseFragment(), getResources().getString(R.string.students));
        adapter.addFragment(new SelectTeacherFragment(), getResources().getString(R.string.teachers));
        viewPager.setAdapter(adapter);
    }

}