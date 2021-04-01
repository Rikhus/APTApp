package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.airbnb.paris.Paris;
import com.rikhus.aptapp.ScheduleNotification.NewScheduleReleasedReciever;

public class SettingsActivity extends ThemedActivity {

    Button lightThemeButton;
    Button darkThemeButton;
    SwitchCompat switchScheduleNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = sharedPreferences.getString("ThemeName", "light");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        lightThemeButton = findViewById(R.id.lightThemeButton);
        darkThemeButton = findViewById(R.id.darkThemeButton);

        // выставляем кнопкам стили в зависимости от того, какая тема активна
        switch (themeName){
            case ("light"):
                Paris.styleBuilder(lightThemeButton).add(R.style.SettingsButtonSelected).apply();
                Paris.styleBuilder(darkThemeButton).add(R.style.SettingsButtonNotSelected).apply();
                break;
            case ("dark"):
                Paris.styleBuilder(lightThemeButton).add(R.style.SettingsButtonNotSelected).apply();
                Paris.styleBuilder(darkThemeButton).add(R.style.SettingsButtonSelected).apply();
                break;
        }


        // манипуляции со свитчем: если уведомления включены, то его тоже нужно включить
        switchScheduleNotification = findViewById(R.id.switchScheduleNotification);
        switchScheduleNotification.setChecked(getIsNotificationServiceEnabled());

        // при изменении состояния свитча
        switchScheduleNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PackageManager pm  = getPackageManager();
                    ComponentName componentName = new ComponentName(SettingsActivity.this, NewScheduleReleasedReciever.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    setIsNotificationServiceEnabled(true);

                    Intent intent = new Intent(SettingsActivity.this, NewScheduleReleasedReciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.CHECK_PERIOD_SECONDS * 1000, pendingIntent);

                    setIsNotificationServiceStarted(true);
                }
                else{
                    PackageManager pm  = getPackageManager();
                    ComponentName componentName = new ComponentName(SettingsActivity.this, NewScheduleReleasedReciever.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                    setIsNotificationServiceEnabled(false);
                }
            }
        });
    }

    private void setIsNotificationServiceEnabled(Boolean isEnabled){
        SharedPreferences preferences = getSharedPreferences("Notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isEnabled", isEnabled);
        editor.apply();
    }

    private void setIsNotificationServiceStarted(Boolean isStarted){
        SharedPreferences preferences = getSharedPreferences("Notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isStarted", isStarted);
        editor.apply();
    }

    private boolean getIsNotificationServiceEnabled(){
        SharedPreferences preferences = getSharedPreferences("Notification", Context.MODE_PRIVATE);
        return  preferences.getBoolean("isEnabled", true);
    }

    public void selectTheme(View view) {
        switch (view.getId()){
            case (R.id.lightThemeButton):
                setThemeName("light");
                break;
            case (R.id.darkThemeButton):
                setThemeName("dark");
                break;
        }
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        this.finish();
    }

    private void setThemeName(String name){
        SharedPreferences preferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ThemeName", name);
        editor.apply();
    }
}