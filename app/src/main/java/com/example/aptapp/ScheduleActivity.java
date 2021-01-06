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
    ScheduleGetter scheduleGetter;
    String groupId;
    String groupName;
    SimpleDateFormat sdf;
    SubjectAdapter adapter;

    private final String DATE_VARIABLE = "DATE_VARIABLE";
    private final String DATA_ADAPTER = "DATA_ADAPTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        // форматирование дат
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        // получение выбранной группы
        Intent groupsIntent = getIntent();
        groupId = groupsIntent.getStringExtra("group_id");
        groupName = groupsIntent.getStringExtra("group_name");

        TextView scheduleForText = findViewById(R.id.scheduleForText);
        String scheduleForString = getResources().getString(R.string.schedule_for) + " " + groupName
                + "(" +groupId + ")";
        scheduleForText.setText(scheduleForString);

        // инициализация графических элементов
        textViewDate = findViewById(R.id.textViewDate);
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // если это активити запущено с нуля
        if (savedInstanceState == null){
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
    }

    // при перевороте телефона нужно чтоб данные сохранялись
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(DATE_VARIABLE, sdf.format(dateAndTime.getTime()));
        outState.putSerializable(DATA_ADAPTER, adapter);
        super.onSaveInstanceState(outState);
    }

    // тут восстанавливаем данные
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {
            dateAndTime.setTime(sdf.parse(savedInstanceState.getString(DATE_VARIABLE)));
            textViewDate.setText(formatDate(dateAndTime));

            if (scheduleGetter != null){
                scheduleGetter.cancel(true);
            }
            adapter = (SubjectAdapter)savedInstanceState.getSerializable(DATA_ADAPTER);
            if (adapter == null) {
                scheduleGetter = new ScheduleGetter();
                scheduleGetter.execute(groupId, sdf.format(dateAndTime.getTime()));
            }
            else{
                scheduleRecyclerView.setAdapter(adapter);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // при выходе из приложения сохраняем данные о текущем расписании (дату, группу)

    @Override
    protected void onStop() {
        FileOutputStream fos = null;

        try{
            // открываем файл и записываем данные
            fos = openFileOutput(Constants.FILENAME, MODE_PRIVATE);
            String data = "group_id: " + groupId +":" +
                    "group_name: " + groupName;
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

        super.onStop();
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

            if (scheduleGetter != null){
                scheduleGetter.cancel(true);
            }
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