package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.paris.Paris;

public class SettingsActivity extends ThemedActivity {

    Button lightThemeButton;
    Button darkThemeButton;

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