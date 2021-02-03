package com.example.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aptapp.Parsing.AptParse;
import com.example.aptapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class GroupSelectActivity extends AppCompatActivity {
    private ArrayList<String> mGroups;
    private ArrayList<String> mGroupsIds;
    private ArrayAdapter adapter;
    private ListView groupsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
        mGroups = new ArrayList<String>();
        mGroupsIds = new ArrayList<String>();

        groupsListView = findViewById(R.id.listViewGroups);
        groupsListView.setDividerHeight(0);
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("group_name", mGroups.get(position));
                intent.putExtra("group_id", mGroupsIds.get(position));

                startActivity(intent);
            }
        });

        int courseNumber = (int)getIntent().getExtras().get("selectedCourse");

        GroupsGetter groupsGetter = new GroupsGetter();
        groupsGetter.execute(courseNumber);

    }

    // получение списка групп в фоновом режиме
    class GroupsGetter extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int courseNumber = integers[0];
            try {
                // получаем список групп
                JsonObject array = AptParse.getGroups().getAsJsonObject("groups");
                // конкретно тут получаем список групп для определенного отделения и курса
                String idStr = "";
                for (JsonElement id : array
                        .getAsJsonArray("dep_0_course_" + courseNumber)) {
                    // тут получаем имена этих групп и запихиваем в массив
                    idStr = id.getAsString().replace("\"", " ");

                    mGroups.add(array.getAsJsonObject(idStr)
                            .get("Name").getAsString());

                    mGroupsIds.add(idStr);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mGroups);
            groupsListView.setAdapter(adapter);
        }
    }
}