package com.example.aptapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.aptapp.Parsing.AptParse;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class GroupSelectActivity extends AppCompatActivity {
    private ArrayList<ArrayList<String>> groupsByDepartaments;
    private ArrayList<ArrayList<String>> groupsByDepartamentsIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);

        int courseNumber = (int)getIntent().getExtras().get("selectedCourse");
        String[] titles = new String[] {getResources().getString(
                R.string.construction_department),
                getResources().getString(R.string.oil_department)};

        ViewPager viewPagerGroups = findViewById(R.id.viewPagerGroups);
        viewPagerGroups.setAdapter(new GroupsFragmentPagerAdapter(getSupportFragmentManager(), courseNumber, titles));

        TabLayout tabLayoutDepartaments = findViewById(R.id.tabLayoutDepartaments);
        tabLayoutDepartaments.setupWithViewPager(viewPagerGroups);

        /*
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("group_name", groupsOil.get(position));
                intent.putExtra("group_id", groupsOilIds.get(position));

                startActivity(intent);
            }
        });

        // запуск процесса получения списка групп
        GroupsGetter constructionGroupsGetter = new GroupsGetter();
        constructionGroupsGetter.execute(0, courseNumber, constructionGroupsList, groupsConstruction, groupsConstructionIds);
*/

    }



}