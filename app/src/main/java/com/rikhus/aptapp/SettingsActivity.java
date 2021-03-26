package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        String themeName = sharedPreferences.getString("ThemeName", "light");

        switch (themeName){
            case ("light"):
                setTheme(R.style.LightTheme);
                break;
            case ("dark"):
                setTheme(R.style.DarkTheme);
                break;
            }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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