package com.example.frederik.au545579weatherapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.icu.util.Calendar;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frederik.au545579weatherapp.Globals;
import com.example.frederik.au545579weatherapp.OverviewActivity;
import com.example.frederik.au545579weatherapp.R;
import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.example.frederik.au545579weatherapp.utils.FromJsonToCityWeatherData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Frederik on 29-03-2018. For SMAP.
 */
public class WeatherDataService extends Service {

    private RequestQueue requestQueue;
    private final IBinder binder = new WeatherDataServiceBinder();
    private List<CityWeatherData> cityWeatherDataList = new ArrayList<>();
    private CityWeatherData myCityData;
    private String myCityName;
    private Context context;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private SharedPreferences sharedPrefs;
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());


    //https://developer.android.com/guide/components/bound-services.html
    public class WeatherDataServiceBinder extends Binder {
        public WeatherDataService getService() {
            // Return this instance of WeatherDataService so clients can call public methods
            return WeatherDataService.this;
        }


    }


    public List<CityWeatherData> getCityWeatherDataList(){
        return cityWeatherDataList;
    }

    //Return the CityWeatherData object that matches the cityName
    public CityWeatherData getCityWeatherData(String cityName){

        for(CityWeatherData element : cityWeatherDataList){

            if(element.name.equals(cityName)){
                return element;
            }

        }

        if(cityName.equals(myCityName))
            return myCityData;

        return null;
    }

    //Returns the users city
    public CityWeatherData getMyCity(){
        return myCityData;

    }

    //Update my city by sending a request with isMyCity true
    public void setMyCity(String cityName){
        sendRequest(cityName, true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(Globals.WEATHER_LOG_TAG, "Started WeatherDataService");

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        myCityName = sharedPrefs.getString(Globals.WEATHER_INTENT_CITY, context.getResources().getString(R.string.default_city));

        // Initialize the RequestQueue for caching and such.
        //https://developer.android.com/training/volley/requestqueue.html

        //Implicit
        requestQueue = Volley.newRequestQueue(context); // Default network and cache settings

        //Explicit steps
        //Cache cache = new DiskBasedCache(context.getCacheDir(), 20*1024*1024); // Get 20MB of cache
        //Network network = new BasicNetwork(new HurlStack());
        //requestQueue = new RequestQueue(cache, network);
        //requestQueue.start();

        //Creates a notification channel and populates it with a notification
        //and starts the service in foreground
        runAsForeground();


        //https://stackoverflow.com/questions/4044726/how-to-set-a-timer-in-java
        //Timer that updates the city info every other minute
        timerTask = new TimerTask() {
            @Override
            public void run() {
                requestAllCities();
            }
        };
        timer.schedule(timerTask, 0, 2*60*1000);





    }

    //https://developer.android.com/reference/android/app/Service.html#onStartCommand(android.content.Intent,%20int,%20int)
    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        // Check if intent is available
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                //Switch on different actions. From notifications WEATHER_UPDATE_ACTION and WEATHER_STOP_ACTION
                switch (action){
                    case Globals.WEATHER_UPDATE_ACTION:
                        requestAllCities();
                        break;

                    case Globals.WEATHER_STOP_ACTION:
                        //If we wanna stop the service, we should remove it from foreground
                        //and stop the timer and stop any requests
                        stopForeground(true);
                        timerTask.cancel();
                        timer.cancel();
                        timer.purge();
                        requestQueue.stop();
                        stopSelf();
                        break;


                }
            }



        }

        Log.d(Globals.WEATHER_LOG_TAG, "WeatherDataService onStartCommand called");
        return START_STICKY;
    }

    //https://developer.android.com/reference/android/app/Service.html#onBind(android.content.Intent)
    //Returns the cityWeatherService binder
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Globals.WEATHER_LOG_TAG, "WeatherDataService onBind called");
        return binder;
    }

    //If data is available, the service broadcasts
    //Used to simplify the interface to the service
    public void broadcastDataIfExists(){
        if(cityWeatherDataList.size() != 0){
            Intent dataIntent = new Intent(Globals.WEATHER_NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(dataIntent);
        }
        if(myCityData != null){
            Intent dataIntent = new Intent(Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(context).sendBroadcast(dataIntent);
        }
    }


    //Nothing to clean up
    @Override
    public void onDestroy() {
        Log.d(Globals.WEATHER_LOG_TAG, "Stopped WeatherDataService");
        super.onDestroy();
    }

    private void runAsForeground(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification();
        }else{
            Log.e(Globals.WEATHER_LOG_TAG, "Only API levels >= 26 supported");
        }
    }

    //https://stackoverflow.com/questions/46990995/on-android-8-1-api-27-notification-does-not-display
    //Creates the notification channel, notification and starts the service in foreground with the notification
    private void createNotification(){


        // Add refresh button in notification.
        Icon refreshIcon = Icon.createWithResource(this, R.drawable.ic_refresh_black_24dp);
        Intent playIntent = new Intent(this, WeatherDataService.class);
        playIntent.setAction(Globals.WEATHER_UPDATE_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        Notification.Action refreshAction = new Notification.Action.Builder(refreshIcon, context.getResources().getString(R.string.refresh), pendingPlayIntent).build();

        //Add stop button to stop foreground.
        Icon stopIcon = Icon.createWithResource(this, R.drawable.ic_close_black_24dp);
        Intent stopIntent = new Intent(this, WeatherDataService.class);
        stopIntent.setAction(Globals.WEATHER_STOP_ACTION);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        Notification.Action stopAction = new Notification.Action.Builder(stopIcon, context.getResources().getString(R.string.stop), pendingStopIntent).build();

        Intent clickIntent = new Intent(this, OverviewActivity.class);
        PendingIntent pendingClickIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification channel
        NotificationChannel notifyChannel = new NotificationChannel(Globals.CHANNEL_ID, Globals.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

        NotificationManager notifyMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notifyMan != null){
            notifyMan.createNotificationChannel(notifyChannel);
        }else{
            Log.e(Globals.WEATHER_LOG_TAG, "Error getting notification manager from system service");
        }

        //https://developer.android.com/guide/topics/ui/notifiers/notifications.html
        Notification.Builder builder = new Notification.Builder(this, Globals.CHANNEL_ID);
        builder.setContentIntent(pendingClickIntent)
                .setContentTitle("Weather Updater Service")
                .setContentText("Last updated: " + timeFormatter.format( Calendar.getInstance().getTime()))
                .setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setChannelId(Globals.CHANNEL_ID)
                .addAction(refreshAction)
                .addAction(stopAction)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .build();

        Notification notification = builder.build();
        startForeground(Globals.NOTIFICATION_ID, notification);
    }

    //https://developer.android.com/reference/android/content/SharedPreferences.html
    private void updateMyCity(CityWeatherData data){
        myCityData = data;
        myCityName = myCityData.name;

        //Save the city
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Globals.WEATHER_INTENT_CITY, myCityName);
        editor.apply();
    }

    //Send a request for all cities
    private void requestAllCities(){
        for(String city : Globals.WEATHER_DEFAULT_CITIES){
            Log.d(Globals.WEATHER_LOG_TAG, "Requesting city: " + city);
            sendRequest(city, false);

        }
        Log.d(Globals.WEATHER_LOG_TAG, "Requesting city: " + myCityName);
        sendRequest(myCityName, true);
    }

    //Request city from openweather
    public void sendRequest(String city, boolean isMyCity){

        sendRequest( city, "dk", isMyCity);
    }

    //Request city from openweather using Volley to handle http request
    public void sendRequest(final String city, String country, final boolean isMyCity) {


        //Build the url string
        String url = Globals.WEATHER_REQUEST_SITE + city + "," + country + Globals.WEATHER_API_KEY;

        //Send request using Volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Upon response try to convert the received string from json to the CityWeatherData object
                        CityWeatherData cityWeatherData = FromJsonToCityWeatherData.parseJsonToCityWeatherData(response);
                        if (cityWeatherData != null) {
                            newCityWeatherData(cityWeatherData, isMyCity);
                            Log.d(Globals.WEATHER_LOG_TAG, response);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Globals.WEATHER_LOG_TAG, "Volley request failed: " + error.getMessage());

                //If there where an error getting the entered city, it probably does not exist
                if(isMyCity){
                    Toast.makeText(context, context.getResources().getString(R.string.wrong_city_err),
                            Toast.LENGTH_LONG).show();
                }

                //http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
                if (error instanceof TimeoutError || (error instanceof NoConnectionError)) {
                    Log.e(Globals.WEATHER_LOG_TAG, "Timeout error");
                } else if (error instanceof NetworkError) {
                    Log.e(Globals.WEATHER_LOG_TAG, "Network error");
                } else if ((error instanceof AuthFailureError) || (error instanceof ServerError)) {
                    Log.e(Globals.WEATHER_LOG_TAG, "Server error");
                }

            }
        });

        //Add the request to the request queue
        requestQueue.add(stringRequest);
    }




    //When new weather data is received it should update the correct city
    private void newCityWeatherData(CityWeatherData cityWeatherData, boolean isMyCity){


        //Update the notification (timestamp)
        createNotification();

        //https://stackoverflow.com/questions/9008532/how-to-find-index-position-of-an-element-in-a-list-when-contains-returns-true
        if(!isMyCity){
            int index;
            index = cityWeatherDataList.indexOf(cityWeatherData);
            if(index == -1){ //The city does not exist in the list it should be added
                cityWeatherDataList.add(cityWeatherData);
            }else{ //The item does exist and should be set
                cityWeatherDataList.set(index, cityWeatherData);
            }
        } else{

           updateMyCity(cityWeatherData);

        }

        //Broadcast that there is new data available to listeners
        if(!isMyCity){
            //https://developer.android.com/guide/components/broadcasts.html
            Intent intent = new Intent(Globals.WEATHER_NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent); //https://developer.android.com/training/run-background-service/report-status.html

        } else{
            Intent intent = new Intent(Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }
}