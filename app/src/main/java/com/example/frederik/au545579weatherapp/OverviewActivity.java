package com.example.frederik.au545579weatherapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.frederik.au545579weatherapp.adapters.CityWeatherAdapter;
import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.example.frederik.au545579weatherapp.services.WeatherDataService;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Bound service
 * https://developer.android.com/guide/components/bound-services.html
 *
 *
 */
public class OverviewActivity extends Activity {

    private WeatherDataService weatherDataService;
    private List<CityWeatherData> cityWeatherDataList;
    private Context context;
    private TextView txtMyCity;
    private TextView txtMyCityTemp;
    private ImageView imgMyCityIcon;
    private CityWeatherData myCityData;
    private WeatherBroadCastReceiver weatherBroadCastReceiver;
    private ListView listCityWeather;
    private Button btnEdit;
    private boolean restoredState = false;
    private CityWeatherAdapter cityWeatherAdapter;
    private int restoredViewIndex = 0;
    private int restoredViewOffset = 0;

    //https://developer.android.com/guide/components/bound-services.html
    private ServiceConnection weatherDataServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WeatherDataService.WeatherDataServiceBinder binder = (WeatherDataService.WeatherDataServiceBinder) service;
            weatherDataService = binder.getService();
            weatherDataService.broadcastDataIfExists();
            Log.d(Globals.WEATHER_LOG_TAG, "ServiceConnection Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(Globals.WEATHER_LOG_TAG, "ServiceConnection disconnected");
        }
    };

    //https://developer.android.com/training/run-background-service/report-status.html
    public class WeatherBroadCastReceiver extends BroadcastReceiver{

        private WeatherBroadCastReceiver() {
        }

        @Override
        public void onReceive(Context con, Intent intent){

            String action = intent.getAction();

            if(action != null){

                //Understand the action of the broadcast and act accordingly
                switch(action){

                    ////There is new myCity data
                    case Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST:
                        //Get data by name of city and update UI
                        updateMyCity();
                        break;

                    case Globals.WEATHER_NEW_DATA_BROADCAST:
                        //There is new data for all other cities
                        //The list used should be cleared so the new data is not added on top of the old
                        //Also notify the adapter that the data has changed
                        cityWeatherDataList.clear();
                        cityWeatherDataList.addAll(weatherDataService.getCityWeatherDataList());
                        cityWeatherAdapter.notifyDataSetChanged();

                        //Restore the list view to the exact same position if onSaveInstanceState was called
                        if(restoredState){
                            listCityWeather.setSelectionFromTop(restoredViewIndex, restoredViewOffset);
                            restoredState = false;
                        }
                        break;

                }

            }

        }

    }

    //Update the views of the cities
    private void updateMyCity(){
        myCityData = weatherDataService.getMyCity();
        if(myCityData != null){

            //Format the double to a string in a humanly readable way
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");

            //Get the information from the cityWeatherData object
            double temp = myCityData.main.temp + Globals.WEATHER_KELVIN_TO_CELSIUS;
            String sTemp = decimalFormat.format(temp) + " \u00b0C";
            String icon = myCityData.weather.get(0).icon;
            int id = context.getResources().getIdentifier("weather_icon_" + icon, "drawable", context.getPackageName());;

            //Update the views
            txtMyCity.setText(myCityData.name);
            txtMyCityTemp.setText(sTemp);
            imgMyCityIcon.setImageResource(id);
            //Picasso.get().load("http://openweathermap.org/img/w/" + icon + ".png").into(imgMyCityIcon);

        }else{
            Log.e(Globals.WEATHER_LOG_TAG, "myCityData is null");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        context =  getApplicationContext();
        weatherBroadCastReceiver = new WeatherBroadCastReceiver();

        //Find views
        txtMyCity = findViewById(R.id.txtMyCity);
        txtMyCityTemp = findViewById(R.id.txtMyCityTemp);
        imgMyCityIcon = findViewById(R.id.imgMyCityIcon);
        btnEdit  = findViewById(R.id.btnEdit);
        listCityWeather = findViewById(R.id.listCityWeather);

        //Initialize list view and assign adapter
        cityWeatherDataList = new ArrayList<>();
        cityWeatherAdapter = new CityWeatherAdapter(context, cityWeatherDataList);
        listCityWeather.setAdapter(cityWeatherAdapter);

        //Add the things we want to receive to the broadcast receiver
        IntentFilter filter = new IntentFilter(Globals.WEATHER_NEW_DATA_BROADCAST);
        filter.addAction(Globals.WEATHER_MY_CITY_NEW_DATA_BROADCAST);

        //Register broadcast receiver
        LocalBroadcastManager.getInstance(context).registerReceiver(weatherBroadCastReceiver, filter);

        //What should be done  when the user clicks the edit button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create an alert asking for city
                createAlert();

            }
        });

        //Listen for clicks on the list view and create details activity
        listCityWeather.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get the cityWeatherData related to the clicked position
                Object object = listCityWeather.getItemAtPosition(position);
                CityWeatherData current = (CityWeatherData) object;

                //Start details activity
                startDetailsActivity(current);

            }
        });

        //Bind to the weather data service and create it if it is not already
        final Intent intent = new Intent(this, WeatherDataService.class);
        bindService(intent, weatherDataServiceConnection, Context.BIND_AUTO_CREATE);

    }

    //Start the details activity
    private void startDetailsActivity(CityWeatherData current){

        //Start the details activity and tell it what city to show info about
        Intent detailsIntent = new Intent(context, DetailsActivity.class);
        detailsIntent.putExtra(Globals.WEATHER_INTENT_CITY, current.name);
        startActivity(detailsIntent);
    }


    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){


        //https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
        //Get the scroll position of the top most view in the list view
        int index = listCityWeather.getFirstVisiblePosition(); //Top most view index
        View v = listCityWeather.getChildAt(0); //Get the top view
        int offset = (v == null) ? 0 : (v.getTop() - listCityWeather.getPaddingTop()); //How much the view is scrolled out of the screen

        //Save the index and offset
        outState.putInt(Globals.LIST_SAVE_INDEX, index);
        outState.putInt(Globals.LIST_SAVE_OFFSET, offset);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){

        //Restore the list view position
        restoredViewIndex = savedInstanceState.getInt(Globals.LIST_SAVE_INDEX);
        restoredViewOffset = savedInstanceState.getInt(Globals.LIST_SAVE_OFFSET);
        restoredState = true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Unbind and remove broadcast receiver
        if (weatherDataServiceConnection != null) {
            //https://stackoverflow.com/questions/1992676/activity-app-name-has-leaked-serviceconnection-serviceconnection-name438030
            unbindService(weatherDataServiceConnection);
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(weatherBroadCastReceiver);
    }

    private void createAlert(){

        //Alert builder with custom style
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.cityDialog));
        builder.setTitle(R.string.new_city_prompt);

        View view = LayoutInflater.from(this).inflate(R.layout.new_city_alert, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = view.findViewById(R.id.input);
        builder.setView(view);

        //When the user clicks the positive button, the input city is added
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCity = input.getText().toString();
                Log.d(Globals.WEATHER_LOG_TAG, "New city: " + input.getText().toString());
                addNewCity(newCity);
            }
        });

        //Cancel the dialog and do nothing else if the user clicks the negative button
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(Globals.WEATHER_LOG_TAG, "Cancelled");
                dialog.cancel();
            }
        });

        builder.show();

    }


    void addNewCity(String city){

        if(weatherDataService != null)
            weatherDataService.setMyCity(city);

    }


}
