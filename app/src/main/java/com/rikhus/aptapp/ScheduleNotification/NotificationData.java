package com.rikhus.aptapp.ScheduleNotification;

import android.content.Context;

import com.rikhus.aptapp.Constants;
import com.rikhus.aptapp.UserType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.FileOutputStream;


public class NotificationData {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void setNotificationsData(Boolean isNotified, Date lastDayNotified, Context context) {
        // сохраняем данные для нотификаций
        try{
            FileOutputStream fos = context.openFileOutput(Constants.NOTIFICATION_FILENAME, Context.MODE_PRIVATE);
            String data = isNotified.toString() + ":" + sdf.format(lastDayNotified);
            fos.write(data.getBytes());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static Date getLastDayNotified(Context context) {
        Date lastDayNotified = new Date(0);
        try{
            FileInputStream fin = context.openFileInput(Constants.NOTIFICATION_FILENAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String data = new String(bytes);
            String dateString = data.split(":")[1];
            lastDayNotified = sdf.parse(dateString);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return lastDayNotified;
    }
    public static Boolean getIsNotified(Context context) {
        boolean isNotified = false;
        try{
            FileInputStream fin = context.openFileInput(Constants.NOTIFICATION_FILENAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String data = new String(bytes);
            String isNotifiedString = data.split(":")[0];
            isNotified = Boolean.parseBoolean(isNotifiedString);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return isNotified;
    }


}
