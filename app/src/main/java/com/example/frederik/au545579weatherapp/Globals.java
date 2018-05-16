package com.example.frederik.au545579weatherapp;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
public class Globals {
    public static final String WEATHER_INTENT_CITY = "com.example.frederik.au545579weatherapp.intent.CITY";
    public static final String WEATHER_STOP_ACTION = "stopWeather";
    public static final String WEATHER_UPDATE_ACTION = "updateWeather";
    public static final String WEATHER_NEW_DATA_BROADCAST = "com.example.frederik.au545579weatherapp.broadcast.WEATHER_BROADCAST";
    public static final String WEATHER_MY_CITY_NEW_DATA_BROADCAST = "com.example.frederik.au545579weatherapp.broadcast.WEATHER_MY_CITY_BROADCAST";
    public static final String WEATHER_REQUEST_SITE = "https://api.openweathermap.org/data/2.5/weather?q=";
    public static final String WEATHER_LOG_TAG = "WeatherApp";
    public static final String WEATHER_API_KEY = "&appid=812f9080c58b2d4b6ff13097e352a2e3";
    public static final String LIST_SAVE_INDEX = "listIndex";
    public static final String LIST_SAVE_OFFSET = "listOffset";
    public static final List<String> WEATHER_DEFAULT_CITIES =
            Arrays.asList("Aarhus", "Aalborg", "Copenhagen",
                    "Odense", "Esbjerg", "Randers", "Kolding",
                    "Horsens", "Vejle", "Roskilde", "Herning");
    public static final String CHANNEL_ID = "id1";
    public static final CharSequence CHANNEL_NAME = "Weather Service";
    public static final int NOTIFICATION_ID = 100;
    public static final double WEATHER_KELVIN_TO_CELSIUS = -273.2;
    public static final double WEATHER_MPH_TO_MS = 0.44704;
}
