package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.io.FileInputStream;

public class FromNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_notification);

        FileInputStream fin = null;

        Intent intent = new Intent(this, ScheduleActivity.class);
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
        catch (Exception ex){
            intent = new Intent(this, SelectUserTypeActivity.class);
        }

        startActivity(intent);
    }
}