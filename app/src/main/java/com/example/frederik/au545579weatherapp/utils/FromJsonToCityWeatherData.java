package com.example.frederik.au545579weatherapp.utils;

import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;
import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Frederik on 03-04-2018. For SMP.
 */
public class FromJsonToCityWeatherData {

    public static CityWeatherData parseJsonToCityWeatherData(String json){
        Gson gson;
        CityWeatherData cityWeatherData;
        try{

            gson = new GsonBuilder().create();
            cityWeatherData = gson.fromJson(json, CityWeatherData.class);

        }catch(JsonParseException e){
            Log.e(Globals.WEATHER_LOG_TAG, "Error parsing response to CityWeatherData");
            Log.e(Globals.WEATHER_LOG_TAG, json);
            return null;
        }
        return cityWeatherData;

    }

}
