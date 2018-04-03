package com.example.frederik.au545579weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;

import java.util.List;

/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
public class WeatherDataService extends Service {

    private final Globals g = new Globals();
    private boolean started = false;
    private final IBinder binder = new WeatherDataServiceBinder();
    private List<CityWeatherData> cityWeatherData;


    public List<CityWeatherData> getCityWeatherData(){
        return cityWeatherData; //@TODO Implement functionality
    }

    public CityWeatherData getCityWeatherData(String cityName){

        return null; //@TODO Implement functionality
    }

    @Override
    public void onCreate() {

        Log.d(g.WEATHER_LOG_TAG, "Started WeatherDataService");
    }

    //https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent,%20int,%20int)
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if (!started && (intent != null)) {

            started = true;
        }
        Log.d(g.WEATHER_LOG_TAG, "WeatherDataService onStartCommand called");
        return START_STICKY;
    }

    //https://developer.android.com/reference/android/app/Service.html#onBind(android.content.Intent)
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(g.WEATHER_LOG_TAG, "WeatherDataService onBind called");
        return binder;
    }

    //https://developer.android.com/guide/components/bound-services.html

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class WeatherDataServiceBinder extends Binder {
        public WeatherDataService getService() {
            // Return this instance of WeatherDataService so clients can call public methods
            return WeatherDataService.this;
        }



    }
    @Override
    public void onDestroy() {
        Log.d(g.WEATHER_LOG_TAG, "Stopped WeatherDataService");
    }
}