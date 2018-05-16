package com.example.frederik.au545579weatherapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.example.frederik.au545579weatherapp.services.WeatherDataService;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {

    private WeatherDataService weatherDataService;
    private TextView txtCity;
    private TextView txtTemp;
    private TextView txtWind;
    private TextView txtDescription;
    private ImageView imgWeatherIcon;
    private Button btnOk;
    private Context context;
    private String city;
    private CityWeatherData cityWeatherData;
    private WeatherBroadCastReceiver weatherBroadCastReceiver;



    private ServiceConnection weatherDataServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WeatherDataService.WeatherDataServiceBinder binder = (WeatherDataService.WeatherDataServiceBinder) service;
            weatherDataService = binder.getService();
            weatherDataService.broadcastDataIfExists();
            Log.d(Globals.WEATHER_LOG_TAG, "ServiceConnection Connected in detailsActivity");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(Globals.WEATHER_LOG_TAG, "ServiceConnection disconnected");
        }
    };


    //https://developer.android.com/training/run-background-service/report-status.html
    public class WeatherBroadCastReceiver extends BroadcastReceiver {

        private WeatherBroadCastReceiver() {
        }

        @Override
        public void onReceive(Context con, Intent intent){

            //Understand the action of the broadcast and act accordingly
            String action = intent.getAction();
            if(action != null){
                switch(action){
                    //There is new myCity data
                    case Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST:
                        //Get data by name of city and update UI
                        cityWeatherData = weatherDataService.getCityWeatherData(city);
                        updateViews();
                        break;

                    //There is new data for all other cities
                    case Globals.WEATHER_NEW_DATA_BROADCAST:
                        cityWeatherData = weatherDataService.getCityWeatherData(city);
                        updateViews();
                        break;
                }
            }
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        context = getApplicationContext();
        Intent intent = getIntent();

        weatherBroadCastReceiver = new WeatherBroadCastReceiver();

        city = intent.getStringExtra(Globals.WEATHER_INTENT_CITY);

        //Views
        txtCity = findViewById(R.id.txtCityNameDe);
        txtTemp = findViewById(R.id.txtTempDe);
        txtDescription = findViewById(R.id.txtDescriptionDe);
        txtWind = findViewById(R.id.txtWindDe);
        imgWeatherIcon = findViewById(R.id.imgWeatherIconDe);
        btnOk = findViewById(R.id.btnOkDe);

        txtCity.setText(city);

        //End the activity with a click of this button
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Add the things we want to receive to the broadcast receiver
        IntentFilter filter = new IntentFilter(Globals.WEATHER_NEW_DATA_BROADCAST);
        filter.addAction(Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST);

        //Register broadcast receiver
        LocalBroadcastManager.getInstance(context).registerReceiver(weatherBroadCastReceiver, filter);

        //Bind to the weather data service
        final Intent weatherServiceIntent = new Intent(context, WeatherDataService.class);
        bindService(weatherServiceIntent, weatherDataServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Unbind from the service if bound
        if (weatherDataServiceConnection != null) {
            //https://stackoverflow.com/questions/1992676/activity-app-name-has-leaked-serviceconnection-serviceconnection-name438030
            unbindService(weatherDataServiceConnection);
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(weatherBroadCastReceiver);
    }


    private void updateViews(){
        if(cityWeatherData == null)
            return;

        //Format the double to a string in a humanly readable way
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");

        //Get the information from the cityWeatherData object
        double temp = cityWeatherData.main.temp + Globals.WEATHER_KELVIN_TO_CELSIUS;
        double wind = cityWeatherData.wind.speed*Globals.WEATHER_MPH_TO_MS;
        String icon = cityWeatherData.weather.get(0).icon;
        String sWind = decimalFormat.format(wind) + " m/s";
        String sTemp = Integer.toString((int) Math.ceil(temp)) + " \u00b0C"; //The weather data is not accurate within decimals in our exact location. Throw them away.

        //Get the id of the, to the icon string, coherent image
        int id = context.getResources().getIdentifier("weather_icon_" + icon, "drawable", context.getPackageName());;

        //Update the views
        txtCity.setText(city);
        txtTemp.setText(sTemp);
        txtWind.setText(sWind);
        txtDescription.setText(cityWeatherData.weather.get(0).description);
        imgWeatherIcon.setImageResource(id);
        //Picasso.get().load("http://openweathermap.org/img/w/" + icon + ".png").into(imgWeatherIcon);

    }


}
