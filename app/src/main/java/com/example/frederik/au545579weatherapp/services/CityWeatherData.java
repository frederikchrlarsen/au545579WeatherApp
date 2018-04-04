package com.example.frederik.au545579weatherapp.services;

import android.graphics.drawable.Drawable;

/**
 * Created by Frederik on 29-03-2018. For SMP.
 */
public class CityWeatherData {
    private String cityName;
    private int temperature;
    private Drawable icon;
    private int wind;
    private String description;
    private int timestamp;


    CityWeatherData(String cityNameP, int temperatureP, String iconP, int windP, String descriptionP, int timestampP){

        setCityName(cityNameP);
        setTemperature(temperatureP);
        setWeatherIcon(iconP);
        setWind(windP);
        setDescription(descriptionP);
        setTimestamp(timestampP);

    }

    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityNameP){
        cityName = cityNameP;
    }

    public int getTemperature(){
        return temperature;
    }
    public void setTemperature(int temperatureP){
        temperature = temperatureP;
    }

    public Drawable getWeatherIcon() {
        return icon;
    }
    public void setWeatherIcon(String iconP){
        //TODO
        //icon = iconP;
    }

    private int getWind(){
        return wind;
    }
    private void setWind(int windP){
        wind = windP;
    }

    private String getDescription(){
        return description;
    }
    private void setDescription(String descriptionP){
        description = descriptionP;
    }

    private int getTimestamp(){
        return timestamp;
    }
    private void setTimestamp(int timestampP){
        timestamp = timestampP;
    }

}
