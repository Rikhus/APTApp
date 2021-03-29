package com.rikhus.aptapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectCourseFragment extends Fragment {



    public SelectCourseFragment() {
        // Required empty public constructor
    }

    public static SelectCourseFragment newInstance() {
        SelectCourseFragment fragment = new SelectCourseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_course,
                container, false);

        Button firstCourseButton = view.findViewById(R.id.firstCourseButton);
        Button secondCourseButton = view.findViewById(R.id.secondCourseButton);
        Button thirdCourseButton = view.findViewById(R.id.thirdCourseButton);
        Button fourthCourseButton = view.findViewById(R.id.fourthCourseButton);
        LinearLayout menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }});


        //onClick для кнопок выбора курса
        View.OnClickListener selectCourseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // нажатие кнопки выбора курса
                int selectedCourse = 0;
                switch (v.getId()){
                    case (R.id.firstCourseButton):
                        selectedCourse = 1;
                        break;
                    case (R.id.secondCourseButton):
                        selectedCourse = 2;
                        break;
                    case (R.id.thirdCourseButton):
                        selectedCourse = 3;
                        break;
                    case (R.id.fourthCourseButton):
                        selectedCourse = 4;
                        break;
                }
                Intent intent = new Intent(getContext(), GroupSelectActivity.class);
                intent.putExtra("selectedCourse", selectedCourse);
                startActivity(intent);
            }
        };

        firstCourseButton.setOnClickListener(selectCourseClickListener);
        secondCourseButton.setOnClickListener(selectCourseClickListener);
        thirdCourseButton.setOnClickListener(selectCourseClickListener);
        fourthCourseButton.setOnClickListener(selectCourseClickListener);

        // Inflate the layout for this fragment
        return view;
    }
}