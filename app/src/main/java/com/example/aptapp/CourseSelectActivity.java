package com.example.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class CourseSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);
    }

    // нажатие кнопки выбора курса
    public void selectCourseClick(View view) {
        int selectedCourse = 0;
        switch (view.getId()){
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
        Intent intent = new Intent(getApplicationContext(), GroupSelectActivity.class);
        intent.putExtra("selectedCourse", selectedCourse);
        startActivity(intent);
    }
}