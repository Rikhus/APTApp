package com.example.aptapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aptapp.Parsing.AptParse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GroupsViewFragment extends Fragment {


    // константы
    private static final String ARG_DEPARTAMENT = "ARG_DEPARTAMENT";
    private static final String ARG_COURSE_NUMBER = "ARG_COURSE_NUMBER";

    // переменные
    private int mDepartament;
    private int mCourseNumber;
    private ArrayList<String> mGroups;
    private ArrayList<String> mGroupsIds;
    View view;

    private ArrayAdapter adapter;

    public GroupsViewFragment() {
        // Required empty public constructor
    }

    public static GroupsViewFragment newInstance(int departamentNumber, int courseNumber) {
        GroupsViewFragment fragment = new GroupsViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEPARTAMENT, departamentNumber);
        args.putInt(ARG_COURSE_NUMBER, courseNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDepartament = getArguments().getInt(ARG_DEPARTAMENT);
            mCourseNumber = getArguments().getInt(ARG_COURSE_NUMBER);

            mGroups = new ArrayList<>();
            mGroupsIds = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Получение фрагмента
        view = inflater.inflate(R.layout.fragment_groups_view, container, false);

        // загрузка списка групп
        GroupsGetter groupsGetter = new GroupsGetter();
        groupsGetter.execute(mDepartament, mCourseNumber);

        // переход на расписание для выбранной группы
        ((ListView)view).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ScheduleActivity.class);
                intent.putExtra("group_name", mGroups.get(position));
                intent.putExtra("group_id", mGroupsIds.get(position));

                startActivity(intent);
            }
        });

        return view;
    }

    // получение списка групп в фоновом режиме
    class GroupsGetter extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int courseNumber = integers[1];
            int departamentNumber = integers[0];
            try {
                // получаем список групп
                JsonObject array = AptParse.getGroups().getAsJsonObject("groups");
                // конкретно тут получаем список групп для определенного отделения и курса

                for (JsonElement id : array
                        .getAsJsonArray("dep_" + departamentNumber + "_course_" + courseNumber)) {
                    // тут получаем имена этих групп и запихиваем в массив
                    mGroups.add(array.getAsJsonObject(id.getAsString()).get("Name").getAsString());
                    mGroupsIds.add(id.getAsString());
                }
            } catch (IOException e) {
                Toast.makeText(getContext(),
                        getResources().getString(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // вывод полученных групп в список
            ListView listViewGroups = (ListView) view;
            adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mGroups);
            listViewGroups.setAdapter(adapter);
        }
    }
}