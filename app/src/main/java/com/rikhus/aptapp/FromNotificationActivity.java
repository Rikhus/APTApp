package com.rikhus.aptapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FromNotificationActivity extends ThemedActivity {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_notification);

        // получаем группу или преподавателя
        SharedPreferences scheduleData = getSharedPreferences("ScheduleData", Context.MODE_PRIVATE);
        String userType = scheduleData.getString("userType", null);
        String id = scheduleData.getString("id", null);
        String name = scheduleData.getString("name", null);

        // получаем дату, на которую следует открыть расписание
        SharedPreferences notificationData = getSharedPreferences("NotificationData", Context.MODE_PRIVATE);
        String dateToOpenSchedule = notificationData.getString("notificationDate", sdf.format(new Date()));


        Intent intent = new Intent(this, ScheduleActivity.class);
;

        if(userType != null && id != null && name != null) {
            intent.putExtra("user_type", UserType.valueOf(userType));
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("date_to_show_schedule", dateToOpenSchedule);
        }
        else {
            intent = new Intent(this, SelectUserTypeActivity.class);
        }
        // открыть расписание на дату, на которое вышло расписание

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}