package com.rikhus.aptapp.ScheduleNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.rikhus.aptapp.Parsing.AptParse;
import com.rikhus.aptapp.R;
import com.rikhus.aptapp.ScheduleActivity;
import com.rikhus.aptapp.UserType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewScheduleReleasedReciever extends BroadcastReceiver {

    private final String CHANNEL_ID = "APTAPP_CHANNEL";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        CheckSchedule checkSchedule = new CheckSchedule();
        checkSchedule.execute();
    }

    private void createNotificationChannel(String channelName, String channelDescription, NotificationManagerCompat nm) {
        // эта процедура нужна лишь для определенных версий сдк
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            nm.createNotificationChannel(channel);
        }
    }

    class CheckSchedule extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            String lastScheduleDayString = "";
            try {
                 lastScheduleDayString = AptParse.getDates().get(0).getAsJsonObject().get("Date").getAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // тут просто получаем последнюю дату
            Date today = new Date();
            Date lastScheduleDay = today;
            try {
                lastScheduleDay = sdf.parse(lastScheduleDayString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // если уведомления на новый день я еще не выводил нужно его вывести
            if (lastScheduleDay.after(NotificationData.getLastDayNotified(context))){
                NotificationData.setNotificationsData(false, lastScheduleDay, context);
            }
            // если появилась новая дата то вышло расписание
            if (lastScheduleDay.after(today)){
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isNewScheduleReleased) {
            if (isNewScheduleReleased && !NotificationData.getIsNotified(context)){
                NotificationData.setNotificationsData(true, NotificationData.getLastDayNotified(context), context);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                createNotificationChannel("APTChannel", "APT app channel", notificationManager);

                Intent myIntent = new Intent(context, ScheduleActivity.class);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);

                NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_apt_for_widget)
                        .setContentTitle("Уведомление!") // title for notification
                        .setContentText("Вышло новое расписание!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true); // clear notification after click

                notificationManager.notify(101, mBuilder.build());
            }
        }
    }
}
