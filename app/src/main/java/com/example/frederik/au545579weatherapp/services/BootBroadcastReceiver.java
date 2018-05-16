package com.example.frederik.au545579weatherapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;

/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
//https://stackoverflow.com/questions/4562734/android-starting-service-at-boot-time
//This receiver catches the boot event, and starts the WeatherDataService.
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if((action != null) && action.equals("android.intent.action.BOOT_COMPLETED")){
            Intent startWeatherService = new Intent(context, WeatherDataService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startWeatherService);
            } else {
                context.startService(startWeatherService);
            }
            Log.d(Globals.WEATHER_LOG_TAG, "Started weather service from boot broadcast");

        }
    }
}
