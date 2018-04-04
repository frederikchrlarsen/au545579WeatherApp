package com.example.frederik.au545579weatherapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;

/**
 * Created by Frederik on 29-03-2018. For SMP.
 */
//https://stackoverflow.com/questions/4562734/android-starting-service-at-boot-time
public class BootBroadcastReceiver extends BroadcastReceiver {
    Globals g = new Globals();
    @Override
    public void onReceive(Context context, Intent intent){
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Intent startWeatherService = new Intent(context, WeatherDataService.class);
            context.startService(startWeatherService);
            Log.d(Globals.WEATHER_LOG_TAG, "Started weather service from boot broadcast");

        }
    }
}
