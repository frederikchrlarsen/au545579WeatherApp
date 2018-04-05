package com.example.frederik.au545579weatherapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.frederik.au545579weatherapp.Globals;
import com.example.frederik.au545579weatherapp.OverviewActivity;
import com.example.frederik.au545579weatherapp.R;
import com.example.frederik.au545579weatherapp.models.CityWeatherData;

import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_LOW;

/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
public class WeatherDataService extends Service {

    private final Globals g = new Globals();
    private boolean started = false;
    private final IBinder binder = new WeatherDataServiceBinder();
    private List<CityWeatherData> cityWeatherData;
    private CityWeatherData myCity;
    private Context context;


    public List<CityWeatherData> getCityWeatherData(){
        return cityWeatherData; //@TODO Implement functionality
    }

    public CityWeatherData getCityWeatherData(String cityName){



        return null; //@TODO Implement functionality
    }

    public CityWeatherData getMyCity(){
        return myCity;

    }

    public void setMyCity(String cityName){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(Globals.WEATHER_LOG_TAG, "Started WeatherDataService");

    }

    //https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent,%20int,%20int)
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        if(intent != null){
            String action = intent.getAction();
            if(action != null && action.equals(Globals.WEATHER_UPDATE_ACTION)){
                Log.d(Globals.WEATHER_LOG_TAG, "Update action received from notification");
            }


        }

        if (!started && (intent != null)) {

            runAsForeground();
            started = true;

        }else{
            Log.d(Globals.WEATHER_LOG_TAG, "Foreground service already started");
        }



        Log.d(Globals.WEATHER_LOG_TAG, "WeatherDataService onStartCommand called");
        return START_STICKY;
    }

    //https://developer.android.com/reference/android/app/Service.html#onBind(android.content.Intent)
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Globals.WEATHER_LOG_TAG, "WeatherDataService onBind called");
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
        started = false;
        super.onDestroy();
        Log.d(Globals.WEATHER_LOG_TAG, "Stopped WeatherDataService");
    }


    private void runAsForeground(){
        // Add Play button intent in notification.
        Icon refreshIcon = Icon.createWithResource(this, R.drawable.ic_refresh_black_24dp);
        Intent playIntent = new Intent(this, WeatherDataService.class);
        playIntent.setAction(Globals.WEATHER_UPDATE_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        Notification.Action refreshAction = new Notification.Action.Builder(refreshIcon, "Refresh", pendingPlayIntent).build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifyChannel = new NotificationChannel(Globals.CHANNEL_ID, Globals.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            NotificationManager notifyMan =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifyMan.createNotificationChannel(notifyChannel);
            Notification.Builder builder = new Notification.Builder(this, Globals.CHANNEL_ID);
            builder.setContentTitle("Test 1")
                    .setContentText("Test 2")
                    .setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                    .setTicker("Test 3")
                    .setChannelId(Globals.CHANNEL_ID)
                    .addAction(refreshAction)
                    .build();

            Notification notification = builder.build();
            startForeground(Globals.NOTIFICATION_ID, notification);
        }else{
            //Handle old API not using the notification channel scheme.
            Log.d(Globals.WEATHER_LOG_TAG, "The code does not work on < 27 api level");
        }
    }
}