package com.example.frederik.au545579weatherapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.example.frederik.au545579weatherapp.models.CityWeather;
import com.example.frederik.au545579weatherapp.services.CityWeatherData;
import com.example.frederik.au545579weatherapp.services.WeatherDataService;
import com.example.frederik.au545579weatherapp.Globals;
import com.example.frederik.au545579weatherapp.utils.FromJsonToCityWeatherData;

import java.util.List;

/**
 * Bound service
 * https://developer.android.com/guide/components/bound-services.html
 *
 *
 */
public class OverviewActivity extends AppCompatActivity {

    private WeatherDataService mService;
    private final Globals g = new Globals();
    private List<CityWeatherData> cityWeatherData;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        Button btnTest  = findViewById(R.id.btnTest);
        Intent intent = new Intent(this, WeatherDataService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backgroundServiceIntent = new Intent(OverviewActivity.this, WeatherDataService.class);
                startService(backgroundServiceIntent);
                sendRequest();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConnection != null) {
            //https://stackoverflow.com/questions/1992676/activity-app-name-has-leaked-serviceconnection-serviceconnection-name438030
            unbindService(mConnection);
        }
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WeatherDataService.WeatherDataServiceBinder binder = (WeatherDataService.WeatherDataServiceBinder) service;
            mService = binder.getService();
            Log.d(Globals.WEATHER_LOG_TAG, "Bound to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(Globals.WEATHER_LOG_TAG, "Disconnected from service");
        }
    };

    private void sendRequest(){
        //send request using Volley
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }
        String url = "https://api.openweathermap.org/data/2.5/weather?q=aarhus,dk&appid=" + Globals.WEATHER_API_KEY;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(Globals.WEATHER_LOG_TAG, "Volley request successful");
                        Log.d(Globals.WEATHER_LOG_TAG, response);
                        CityWeather cityWeather = FromJsonToCityWeatherData.parseJsonToCityWeatherData(response);
                        if(cityWeather != null)
                            Log.d(Globals.WEATHER_LOG_TAG, "The temp in Aarhus: " + cityWeather.main.temp );

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(Globals.WEATHER_LOG_TAG, "Volley request failed: " + error.getMessage());

                //Error handling??
                //http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
                if(error instanceof TimeoutError){
                    Log.d(Globals.WEATHER_LOG_TAG, "Timeout error");
                }else if((error instanceof NetworkError) || (error instanceof NoConnectionError)){
                    Log.d(Globals.WEATHER_LOG_TAG, "Network error");
                }else if((error instanceof AuthFailureError)  || (error instanceof ServerError)){
                    Log.d(Globals.WEATHER_LOG_TAG, "Server error");
                }

            }
        });

        queue.add(stringRequest);

    }
}
