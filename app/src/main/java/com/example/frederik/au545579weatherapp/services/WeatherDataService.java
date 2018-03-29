package com.example.frederik.au545579weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;

/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
public class WeatherDataService extends Service {

    private Globals g = new Globals();
    private boolean started = false;

    @Override
    public void onCreate(){

        Log.d(g.WEATHER_LOG_TAG, "onCreate Called");
    }

    //https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent,%20int,%20int)
    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        if(!started && (intent != null)){

            started = true;
        }
        Log.d(g.WEATHER_LOG_TAG, "onStartCommand called");
        return START_STICKY;
    }

    //https://developer.android.com/reference/android/app/Service.html#onBind(android.content.Intent)
    @Override
    public IBinder onBind(Intent intent){
        Log.d(g.WEATHER_LOG_TAG, "onBind called");
        return null;
    }


    @Override
    public void onDestroy(){
        Log.d(g.WEATHER_LOG_TAG, "WeatherDataService destroyed!");
    }
}
