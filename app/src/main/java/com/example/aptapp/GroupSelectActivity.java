package com.example.aptapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class GroupSelectActivity extends AppCompatActivity {
    private ArrayList<String> groupsConstruction = new ArrayList<>();
    private ArrayList<String> groupsOil = new ArrayList<>();
    private ArrayList<String> groupsConstructionIds = new ArrayList<>();
    private ArrayList<String> groupsOilIds = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);

        int courseNumber = (int)getIntent().getExtras().get("selectedCourse");

        // переход на расписание выбранной группы
        ListView constructionGroupsList = findViewById(R.id.constructionGroupsList);
        constructionGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("group_name", groupsConstruction.get(position));
                intent.putExtra("group_id", groupsConstructionIds.get(position));

                startActivity(intent);
            }
        });

        ListView oilGroupsList = findViewById(R.id.oilGroupsList);
        oilGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("group_name", groupsOil.get(position));
                intent.putExtra("group_id", groupsOilIds.get(position));

                startActivity(intent);
            }
        });

        // работа со вкладками
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tabConstruction");
        tabSpec.setIndicator(getResources().getString(R.string.construction_department));
        tabSpec.setContent(R.id.constructionGroupsList);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tagOil");
        tabSpec.setIndicator(getResources().getString(R.string.oil_department));
        tabSpec.setContent(R.id.oilGroupsList);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tagConstruction");

        // запуск процесса получения списка групп
        GroupsGetter constructionGroupsGetter = new GroupsGetter();
        constructionGroupsGetter.execute(0, courseNumber, constructionGroupsList, groupsConstruction, groupsConstructionIds);

        GroupsGetter oilGroupsGetter = new GroupsGetter();
        oilGroupsGetter.execute(1, courseNumber, oilGroupsList, groupsOil, groupsOilIds);
    }

    // получение списка групп в фоновом режиме
    class GroupsGetter extends AsyncTask<Object, Void, Void> {

        int depId = 0;
        int courseNumber = 0;
        ListView groupsList;
        ArrayList<String> groups;
        ArrayList<String> groupsIds;

        @Override
        protected Void doInBackground(Object... objects) {
            try {
                depId = (int)objects[0];
                courseNumber = (int)objects[1];
                groupsList = (ListView)objects[2];
                groups = (ArrayList) objects[3];
                groupsIds = (ArrayList)objects[4];
                // получаем список групп
                JsonObject array = AptParse.getGroups().getAsJsonObject("groups");
                // конкретно тут получаем список групп для определенного отделения и курса
                for (JsonElement id : array
                        .getAsJsonArray("dep_"+(int)objects[0]+"_course_"+((int)objects[1]))){
                    // тут получаем имена этих групп и запихиваем в массив

                    groups.add(array.getAsJsonObject(id.getAsString()).get("Name").getAsString());
                    groupsIds.add(id.getAsString());

                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            // запихиваем массив с группами в ListView
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (GroupSelectActivity.this, android.R.layout.simple_list_item_1,
                            groups);
            ListView groupsView = groupsList;
            groupsView.setAdapter(adapter);
        }
    }
}