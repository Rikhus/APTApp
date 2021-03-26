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

public class SelectUserTypeActivity extends AppCompatActivity {

    TabLayout userTypeTabLayout;
    ViewPager fragmentChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = sharedPreferences.getString("ThemeName", "light");

        switch (themeName){
            case ("light"):
                setTheme(R.style.LightTheme);
                break;
            case ("dark"):
                setTheme(R.style.DarkTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        // если до этого открывалось расписание это не перезагрузка активити при перевороте телефона перекидываем на ту же группу
        if (savedInstanceState == null) {
            FileInputStream fin = null;
            try {

                fin = openFileInput(Constants.FILENAME);
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                String text = new String(bytes);
                if (text.equals("")){
                    throw new Exception();
                }
                String userType = text.split(":")[0];

                String groupId = "";
                String groupName = "";
                String teacherid = "";
                String teacherName = "";

                Intent intent = new Intent(this, ScheduleActivity.class);

                if(userType.equals("STUDENT")){
                    groupId = text.split(":")[1];
                    groupName = text.split(":")[2];
                    groupId = groupId.replace(" ", "");
                    groupName = groupName.replace(" ", "");
                    intent.putExtra("user_type", UserType.STUDENT);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("group_name", groupName);
                }
                else if (userType.equals("TEACHER")){
                    teacherid = text.split(":")[1];
                    teacherName = text.split(":")[2];
                    intent.putExtra("user_type", UserType.TEACHER);
                    intent.putExtra("teacher_id", teacherid);
                    intent.putExtra("teacher_name", teacherName);
                }
                else{
                    throw new Exception();
                }

                startActivity(intent);
            }
            catch(Exception ex){}
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