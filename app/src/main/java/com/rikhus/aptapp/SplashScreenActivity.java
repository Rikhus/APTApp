package com.rikhus.aptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 500;

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
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent (SplashScreenActivity.this, SelectUserTypeActivity.class);
                startActivity(i);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}