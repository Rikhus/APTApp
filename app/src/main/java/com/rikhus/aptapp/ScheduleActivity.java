package com.rikhus.aptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rikhus.aptapp.Parsing.AptParse;
import com.rikhus.aptapp.Parsing.Subject;
import com.rikhus.aptapp.Parsing.SubjectAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {
    RecyclerView scheduleRecyclerView;
    Calendar dateAndTime = Calendar.getInstance();
    TextView textViewDate;
    TextView scheduleForText;
    LinearLayout dateSelectMenu;

    ScheduleGetter scheduleGetter;

    UserType userType;
    String groupId;
    String groupName;
    String teacherId;
    String teacherName;

    SimpleDateFormat sdf;
    SubjectAdapter adapter;

    LinearLayout.LayoutParams dateMenuParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            2.0f
    );
    LinearLayout.LayoutParams scheduleForTextParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
    );
    LinearLayout.LayoutParams scheduleRecyclerViewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            12.0f
    );

    private final String DATE_VARIABLE = "DATE_VARIABLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        // форматирование дат
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        // получение выбранной группы
        Intent groupsIntent = getIntent();

        userType = (UserType) groupsIntent.getSerializableExtra("user_type");

        if (userType == UserType.STUDENT){
            groupId = groupsIntent.getStringExtra("group_id");
            groupName = groupsIntent.getStringExtra("group_name");
        }
        else{
            teacherId = groupsIntent.getStringExtra("teacher_id");
            teacherName = groupsIntent.getStringExtra("teacher_name");
        }

        // сохраняем выбранную группу
        FileOutputStream fos = null;
        try{
            // открываем файл и записываем данные
            fos = openFileOutput(Constants.FILENAME, MODE_PRIVATE);
            String data = "";
            if (userType == UserType.STUDENT){
                data = "STUDENT:" + groupId + ":" + groupName;
            }
            else{
                data = "TEACHER:" + teacherId + ":" + teacherName;
            }

            fos.write(data.getBytes());
        }
        catch (IOException ex){
            System.out.println("error while writing to file");
        }
        finally {
            try{
                if (fos != null) fos.close();
            }
            catch (IOException ex){
                System.out.println("error while closing file stream");
            }
        }

        scheduleForText = findViewById(R.id.scheduleForText);
        String scheduleForString = "";
        if (userType == UserType.STUDENT){
            scheduleForString = getResources().getString(R.string.schedule_for) + " " + groupName;
        }
        else{
            scheduleForString = teacherName;
        }
        scheduleForText.setText(scheduleForString);

        // инициализация графических элементов
        textViewDate = findViewById(R.id.textViewDate);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dateSelectMenu = findViewById(R.id.dateSelectMenu);
        scheduleRecyclerViewParams.leftMargin = 20;
        scheduleRecyclerViewParams.rightMargin = 20;

        // если это активити запущено с нуля
        if (savedInstanceState == null){
            adapter = new SubjectAdapter();
            scheduleRecyclerView.setAdapter(adapter);

            // по умолчанию расписание на сегодня
            dateAndTime.setTime(new Date());
            textViewDate.setText(formatDate(dateAndTime));

            // запуск асинхронной загрузки данных
            scheduleGetter = new ScheduleGetter();
            if (userType == UserType.STUDENT){
                scheduleGetter.execute(groupId, sdf.format(new Date()));
            }
            else{
                scheduleGetter.execute(teacherId, sdf.format(new Date()));
            }

        }
    }

    // при перевороте телефона нужно чтоб данные сохранялись
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // тут запоминаем дату
        outState.putString(DATE_VARIABLE, sdf.format(dateAndTime.getTime()));

        // засовываем адаптер в класс с константами
        if (adapter != null) Constants.adapter = adapter;

        super.onSaveInstanceState(outState);
    }

    // тут восстанавливаем данные
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // немного изменяем разметку для лучшего отображения
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            dateMenuParams.weight = 2.0f;

            scheduleForTextParams.bottomMargin = 10;
            scheduleForTextParams.topMargin = 10;

            scheduleRecyclerViewParams.topMargin = 10;
            scheduleRecyclerViewParams.bottomMargin = 20;

            dateSelectMenu.setLayoutParams(dateMenuParams);
            scheduleForText.setLayoutParams(scheduleForTextParams);
            scheduleRecyclerView.setLayoutParams(scheduleRecyclerViewParams);
        }
        else if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            dateMenuParams.weight = 3.0f;
            scheduleForTextParams.bottomMargin = 5;
            scheduleForTextParams.topMargin = 5;
            scheduleRecyclerViewParams.topMargin = 0;
            scheduleRecyclerViewParams.bottomMargin = 0;

            dateSelectMenu.setLayoutParams(dateMenuParams);
            scheduleForText.setLayoutParams(scheduleForTextParams);
            scheduleRecyclerView.setLayoutParams(scheduleRecyclerViewParams);
        }

        try {
            dateAndTime.setTime(sdf.parse(savedInstanceState.getString(DATE_VARIABLE)));
            textViewDate.setText(formatDate(dateAndTime));

            if (scheduleGetter != null){
                scheduleGetter.cancel(true);
            }
            adapter = Constants.adapter;
            if (adapter == null) {
                scheduleGetter = new ScheduleGetter();
                if(userType == UserType.STUDENT){
                    scheduleGetter.execute(groupId, sdf.format(dateAndTime.getTime()));
                }
                else{
                    scheduleGetter.execute(teacherId, sdf.format(dateAndTime.getTime()));
                }
            }
            else{
                scheduleRecyclerView.setAdapter(adapter);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // выбор даты
    public void selectDate(View view) {
        new DatePickerDialog(ScheduleActivity.this, R.style.DialogTheme, selectDateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // запихиваем выбранную дату в dateAndTime
    DatePickerDialog.OnDateSetListener selectDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            textViewDate.setText(formatDate(dateAndTime));

            if (scheduleGetter != null){
                scheduleGetter.cancel(true);
            }
            scheduleGetter = new ScheduleGetter();
            if (userType == UserType.STUDENT){
                scheduleGetter.execute(groupId, sdf.format(dateAndTime.getTime()));
            }
            else{
                scheduleGetter.execute(teacherId, sdf.format(dateAndTime.getTime()));
            }
        }
    };

    // форматирование вывода даты
    public String formatDate(Calendar dateAndTime){
        return DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    // получения расписания асинхронно
    class ScheduleGetter extends AsyncTask<String, Void, ArrayList<Subject>>{

        @Override
        protected ArrayList<Subject> doInBackground(String... strings) {
            try {
                if (userType == UserType.STUDENT){
                    return AptParse.getSchedule(strings[0], strings[1]);
                }
                else{
                    return AptParse.getScheduleForTeacher(strings[0], strings[1]);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Subject> schedule){
            if(schedule == null){
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.connection_error),
                        Toast.LENGTH_LONG).show();
                return;
            }

            adapter.clearItems();
            adapter.setItems(schedule);
        }
    }
}