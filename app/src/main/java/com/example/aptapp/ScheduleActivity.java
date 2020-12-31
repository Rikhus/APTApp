/* TODO:
сделать выбор дат
сделать разделение по подгруппам
сделать получше дизайн
попробовать оптимизировать
*/
package com.example.aptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aptapp.Parsing.AptParse;
import com.example.aptapp.Parsing.Subject;
import com.example.aptapp.Parsing.SubjectAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {
    RecyclerView scheduleRecyclerView;
    Calendar dateAndTime = Calendar.getInstance();
    TextView textViewDate;
    ScheduleGetter scheduleGetter;
    String groupId;
    SimpleDateFormat sdf;
    SubjectAdapter adapter;

    private final String DATE_VARIABLE = "DATE_VARIABLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        // форматирование дат
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        // получение выбранной группы
        Intent groupsIntent = getIntent();
        groupId = groupsIntent.getStringExtra("group_id");
        TextView scheduleForText = findViewById(R.id.scheduleForText);
        scheduleForText.setText(getResources().getString(R.string.schedule_for) + " " +
                groupsIntent.getStringExtra("group_name") + "(" +groupId + ")");

        // инициализация графических элементов
        textViewDate = findViewById(R.id.textViewDate);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter();
        scheduleRecyclerView.setAdapter(adapter);

        // по умолчанию расписание на сегодня
        dateAndTime.setTime(new Date());
        textViewDate.setText(formatDate(dateAndTime));

        // запуск асинхронной загрузки данных
        scheduleGetter = new ScheduleGetter();
        scheduleGetter.execute(
                groupsIntent.getStringExtra("group_id"),
                sdf.format(new Date()));

    }

    // при перевороте телефона нужно чтоб дата сохранялась
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(DATE_VARIABLE, sdf.format(dateAndTime.getTime()));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {
            dateAndTime.setTime(sdf.parse(savedInstanceState.getString(DATE_VARIABLE)));
            textViewDate.setText(formatDate(dateAndTime));

            scheduleGetter.cancel(true);
            scheduleGetter = new ScheduleGetter();
            scheduleGetter.execute(groupId, sdf.format(dateAndTime.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // выбор даты
    public void selectDate(View view) {
        new DatePickerDialog(ScheduleActivity.this, selectDateListener,
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


            scheduleGetter.cancel(true);
            scheduleGetter = new ScheduleGetter();
            scheduleGetter.execute(groupId, sdf.format(dateAndTime.getTime()));
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
                return AptParse.getSchedule(strings[0], strings[1]);
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