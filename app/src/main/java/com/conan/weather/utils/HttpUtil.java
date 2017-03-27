package com.conan.weather.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author        JY
 * PublishDate   2017-03-27
 * Description   网络请求工具类
 * Version       1.0
 * Updated       JY
 */
public class HttpUtil {

    private static String MAIN_URL = "http://guolin.tech/";
    public static HttpService http() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MAIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(HttpService.class);
    }
}
