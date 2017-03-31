package com.conan.weather.utils;

import com.conan.weather.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Author        JY
 * PublishDate   2017-03-27
 * Description   网络请求工具类
 * Version       1.0
 * Updated       JY
 */
public class HttpUtil {

    private static String MAIN_URL = "http://guolin.tech/";
    private static OkHttpClient.Builder builder;

    public static Retrofit http() {
        okHttpClientBuilder();
        return new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(MAIN_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit httpString() {
        okHttpClientBuilder();
        return new Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    private static void okHttpClientBuilder() {
        builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
    }
}
