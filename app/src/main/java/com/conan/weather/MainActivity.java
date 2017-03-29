package com.conan.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.conan.weather.activity.WeatherHomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String weatherId = preferences.getString("weatherId", "0");
        if (!"0".equals(weatherId)) {
            WeatherHomeActivity.instance(this, weatherId);
            finish();
        }
    }
}
