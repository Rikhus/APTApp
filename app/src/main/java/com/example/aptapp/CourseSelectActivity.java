package com.example.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CourseSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_select);

        ListView coursesList = findViewById(R.id.coursesList);

        coursesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GroupSelectActivity.class);

                int selectedCourse = 0;

                switch(parent.getItemAtPosition(position).toString()){
                    case "1 курс":
                        selectedCourse = 1;
                        break;
                    case "2 курс":
                        selectedCourse = 2;
                        break;
                    case "3 курс":
                        selectedCourse = 3;
                        break;
                    case "4 курс":
                        selectedCourse = 4;
                        break;
                    default:
                        Toast.makeText(CourseSelectActivity.this, "Выбран неверный курс", Toast.LENGTH_LONG).show();
                        return;
                }
                intent.putExtra("selectedCourse", selectedCourse);
                startActivity(intent);
            }
        });
    }

}