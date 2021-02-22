package com.rikhus.aptapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.rikhus.aptapp.Parsing.AptParse;
import com.rikhus.aptapp.Parsing.Group;
import com.rikhus.aptapp.Parsing.Teacher;

import java.util.ArrayList;

public class SelectTeacherFragment extends Fragment {

    ArrayList<Teacher> teachers = new ArrayList<>();
    ArrayList<Teacher> teachersToShow = new ArrayList<>();
    ArrayList<String> teachersNames = new ArrayList<>();
    ArrayAdapter adapter;
    ListView listViewTeachers;
    TextInputEditText textInputTeacherName;

    public SelectTeacherFragment() {
        // Required empty public constructor
    }


    public static SelectTeacherFragment newInstance(String param1, String param2) {
        SelectTeacherFragment fragment = new SelectTeacherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_teacher, container, false);
        listViewTeachers = view.findViewById(R.id.listViewTeachers);
        listViewTeachers.setDividerHeight(0);

        // при выборе преподавателя
        listViewTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ScheduleActivity.class);
                intent.putExtra("user_type", UserType.TEACHER);
                intent.putExtra("teacher_id", teachersToShow.get(position).getId());
                intent.putExtra("teacher_name", teachersToShow.get(position).getName());

                startActivity(intent);
            }
        });

        // при нажатии ентер в поле поиска
        textInputTeacherName = view.findViewById(R.id.textInputTeacherName);
        textInputTeacherName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // только после отжатия кнопки ентер
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER){

                    // очищаем коллекции
                    teachersToShow.clear();
                    teachersNames.clear();

                    // находим вхождения строки поиска в списке учителей
                    String searchedTeacherName = textInputTeacherName.getText().toString();
                    for (Teacher teacher : teachers){
                        if (teacher.getName().contains(searchedTeacherName)){
                            teachersToShow.add(teacher);
                            teachersNames.add(teacher.getName());
                        }
                    }

                    // применяем изменения списка в адаптере
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        TeachersGetter teachersGetter = new TeachersGetter();
        teachersGetter.execute();
        return view;
    }

    // получение списка групп в фоновом режиме
    class TeachersGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // получаем список групп
                teachers = AptParse.getTeachers();
                teachersToShow.addAll(teachers);

            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                getResources().getString(R.string.connection_error),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            teachersNames = new ArrayList<>();
            for (Teacher teacher : teachersToShow){
                teachersNames.add(teacher.getName());
            }
            adapter = new ArrayAdapter(getContext(), R.layout.select_group_item_view, teachersNames);
            listViewTeachers.setAdapter(adapter);
        }
    }
}