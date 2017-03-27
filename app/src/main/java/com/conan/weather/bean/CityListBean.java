package com.conan.weather.bean;

/**
 * Author        JY
 * PublishDate   2017-03-27
 * Description   城市列表总类
 * Version       1.0
 * Updated       JY
 */
public class CityListBean {

    /**
     * id : 831
     * name : 烟台
     * weather_id : CN101120501
     */

    private int id;
    private String name;
    private String weather_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
