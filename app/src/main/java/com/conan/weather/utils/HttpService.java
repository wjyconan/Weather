package com.conan.weather.utils;

import com.conan.weather.bean.CityListBean;
import com.conan.weather.bean.WeatherBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Author        JY
 * PublishDate   2017-03-27
 * Description   功能描述
 * Version       1.0
 * Updated       JY
 */
public interface HttpService {

    @GET("/api/china/")
    Observable<List<CityListBean>> getProvince();

    @GET("/api/china/{pid}")
    Observable<List<CityListBean>> getCity(@Path("pid") int pid);

    @GET("/api/china/{pid}/{cid}")
    Observable<List<CityListBean>> getCounty(@Path("pid") int pid, @Path("cid") int cid);

    @GET("/api/weather?key=dff005fabf5040bfbc5bd34f89426b88")
    Observable<WeatherBean> getWeather(@Query("cityid") String cityId);

    @GET("/api/bing_pic")
    Observable<String> getBingPic();
}
