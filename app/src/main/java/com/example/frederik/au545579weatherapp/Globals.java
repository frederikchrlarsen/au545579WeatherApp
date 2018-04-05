package com.example.frederik.au545579weatherapp;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Frederik on 29-03-2018. For SMP.
 */
public class Globals {
    public static final String WEATHER_UPDATE_ACTION = "updateWeather";
    public static final String WEATHER_LOG_TAG = "WeatherApp";
    public static final String WEATHER_API_KEY = "812f9080c58b2d4b6ff13097e352a2e3";
    public static final List<String> WEATHER_DEFAULT_CITIES =
            Arrays.asList("Aarhus", "Aalborg", "Copenhagen",
                    "Odense", "Esbjerg", "Randers", "Kolding",
                    "Horsens", "Vejle", "Roskilde", "Herning");

    public static final int SERVICE_IMPORTANCR = 1;
    public static final String CHANNEL_ID = "id1";
    public static final CharSequence CHANNEL_NAME = "Weather Service";
    public static final int NOTIFICATION_ID = 100;
}
