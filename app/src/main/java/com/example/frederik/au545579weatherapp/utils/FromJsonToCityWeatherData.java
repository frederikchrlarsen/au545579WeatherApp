package com.example.frederik.au545579weatherapp.utils;

import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Frederik on 03-04-2018. For SMP.
 */
public class FromJsonToCityWeatherData {

    public static CityWeatherData parseJsonToCityWeatherData(String json){

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, CityWeatherData.class);

    }

}
