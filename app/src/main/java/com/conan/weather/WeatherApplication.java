package com.conan.weather;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Author        JY
 * PublishDate   2017-03-27
 * Description   功能描述
 * Version       1.0
 * Updated       JY
 */
public class WeatherApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
