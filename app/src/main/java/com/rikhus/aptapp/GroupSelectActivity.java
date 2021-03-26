package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rikhus.aptapp.Parsing.AptParse;
import com.rikhus.aptapp.Parsing.Group;
import com.rikhus.aptapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class GroupSelectActivity extends AppCompatActivity {
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView groupsListView;

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
        setContentView(R.layout.activity_group_select);


        groupsListView = findViewById(R.id.listViewGroups);
        groupsListView.setDividerHeight(0);
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                intent.putExtra("user_type", UserType.STUDENT);
                intent.putExtra("group_name", groups.get(position).getGroupName());
                intent.putExtra("group_id", groups.get(position).getGroupId());

                startActivity(intent);
                GroupSelectActivity.this.finish();
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
                groups = AptParse.getGroups(courseNumber);

            } catch (Exception ex) {
                GroupSelectActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.connection_error),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> groupsNames = new ArrayList<>();
            for (Group group : groups){
                groupsNames.add(group.getGroupName());
            }
            adapter = new ArrayAdapter(GroupSelectActivity.this, R.layout.select_group_item_view, groupsNames);
            groupsListView.setAdapter(adapter);
        }
    }
}