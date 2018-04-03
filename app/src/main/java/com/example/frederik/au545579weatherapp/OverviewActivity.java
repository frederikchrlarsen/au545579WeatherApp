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

import com.example.frederik.au545579weatherapp.services.CityWeatherData;
import com.example.frederik.au545579weatherapp.services.WeatherDataService;
import com.example.frederik.au545579weatherapp.Globals;

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
            }
        });
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WeatherDataService.WeatherDataServiceBinder binder = (WeatherDataService.WeatherDataServiceBinder) service;
            mService = binder.getService();
            Log.d(g.WEATHER_LOG_TAG, "Bound to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(g.WEATHER_LOG_TAG, "Disconnected from service");
        }
    };
}
