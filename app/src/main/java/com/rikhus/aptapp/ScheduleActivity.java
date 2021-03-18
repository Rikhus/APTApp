package com.rikhus.aptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import com.rikhus.aptapp.ScheduleNotification.NewScheduleReleasedReciever;
import com.rikhus.aptapp.ScheduleNotification.NotificationData;

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

    LinearLayout.LayoutParams dateMenuParams;
    LinearLayout.LayoutParams scheduleForTextParams;
    LinearLayout.LayoutParams scheduleRecyclerViewParams;

    private final String DATE_VARIABLE = "DATE_VARIABLE";
    private final int CHECK_PERIOD_SECONDS = 300;
    //private final int CHECK_PERIOD_SECONDS = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // форматирование дат
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        // получение выбранной группы или преподавателя
        Intent groupsIntent = getIntent();

        userType = (UserType) groupsIntent.getSerializableExtra("user_type");

        // в зависимости от типа пользователя получаем его данные
        if (userType == UserType.STUDENT){
            groupId = groupsIntent.getStringExtra("group_id");
            groupName = groupsIntent.getStringExtra("group_name");
        }
        else{
            teacherId = groupsIntent.getStringExtra("teacher_id");
            teacherName = groupsIntent.getStringExtra("teacher_name");
        }

        // сохраняем тип пользователя и его айди и настраиваем лейауты для перевернутого экрана
        saveData();
        setupLayoutsForViews();

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
        dateSelectMenu = findViewById(R.id.dateSelectMenu);
        scheduleRecyclerViewParams.leftMargin = 20;
        scheduleRecyclerViewParams.rightMargin = 20;

        // если это активити запущено с нуля
        if (savedInstanceState == null){
            adapter = new SubjectAdapter();
            scheduleRecyclerView.setAdapter(adapter);

            // если не указана дата (т.е не запущено с уведомления)
            if (groupsIntent.getStringExtra("date_to_show_schedule") == null) {
                // запуск автоматического подбора даты и расписания
                ScheduleParsingStarter scheduleParsingStarter = new ScheduleParsingStarter();

                if (userType == UserType.STUDENT) {
                    scheduleParsingStarter.execute(groupId);
                }
                if (userType == UserType.TEACHER) {
                    scheduleParsingStarter.execute(teacherId);
                }
            }

            // если запущено с уведомления
            else{
                // открываем расписание на следующий день
                String dateToShowScheduleString = groupsIntent.getStringExtra("date_to_show_schedule");
                ScheduleGetter scheduleGetter = new ScheduleGetter();
                if (userType == UserType.STUDENT) {
                    scheduleGetter.execute(groupId, dateToShowScheduleString);
                }
                if (userType == UserType.TEACHER) {
                    scheduleGetter.execute(teacherId, dateToShowScheduleString);
                }
                try {
                    // выводим дату на экране
                    Calendar dateToShowSchedule = Calendar.getInstance();
                    dateToShowSchedule.setTime(sdf.parse(dateToShowScheduleString));
                    textViewDate.setText(formatDate(dateToShowSchedule));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        // создание аларма и уведомления о новом расписании
        Intent intent = new Intent(this, NewScheduleReleasedReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), CHECK_PERIOD_SECONDS * 1000, pendingIntent);
    }

    public void setupLayoutsForViews(){
        // переменные разметки для изменения активити при перевороте экрана
        dateMenuParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                2.0f
        );
         scheduleForTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        scheduleRecyclerViewParams  = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                12.0f
        );
    }

    public void saveData(){
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

    // получения времени окончания последней пары
    class ScheduleParsingStarter extends AsyncTask<String, Void, String[]>{
        @Override
        protected String[] doInBackground(String... strings) {
            try {

                Calendar currentDate = Calendar.getInstance();

                Date pairsEndTime = AptParse.getPairsEndTime(strings[0], sdf.format(currentDate.getTime()), userType);
                Date currentTime = Calendar.getInstance().getTime();

                // если последняя пара кончилась открываем расписание на следующий день
                if (currentTime.getTime() > pairsEndTime.getTime()){
                    // достаем последний день, на который есть расписание
                    String lastSubjectDay = AptParse.getDates().get(0).getAsJsonObject().get("Date").getAsString();
                    currentDate.setTime(sdf.parse(lastSubjectDay));
                }
                String currentDateString = sdf.format(currentDate.getTime());

                return new String[] {strings[0], currentDateString};

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new String[] {strings[0], sdf.format(new Date())};
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {

            // запуск асинхронной загрузки данных
            scheduleGetter = new ScheduleGetter();
            scheduleGetter.execute(strings[0], strings[1]);

            try {
                dateAndTime.setTime(sdf.parse(strings[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            textViewDate.setText(formatDate(dateAndTime));
        }
    }
}