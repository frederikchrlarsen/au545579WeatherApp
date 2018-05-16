package com.example.frederik.au545579weatherapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frederik.au545579weatherapp.Globals;
import com.example.frederik.au545579weatherapp.R;
import com.example.frederik.au545579weatherapp.models.CityWeatherData;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Frederik on 12-04-2018. For SMP.
 */
public class CityWeatherAdapter extends BaseAdapter {

    private Context context;
    private List<CityWeatherData> cityWeatherDataList;

    public CityWeatherAdapter(Context c, List<CityWeatherData> list){

        this.context = c;
        this.cityWeatherDataList = list;

    }

    private class ViewHolder {
        public TextView txtCityName;
        TextView txtTemp;
        ImageView imgWeather;

        public ViewHolder(View view) {
            txtCityName = view.findViewById(R.id.txtCityName);
            txtTemp = view.findViewById(R.id.txtTemp);
            imgWeather = view.findViewById(R.id.imgWeather);
        }
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup){
        ViewHolder viewHolder; // Used to reduce findViewById calls

        if(view == null){ // Create view if not existing

            view = LayoutInflater.from(context).inflate(R.layout.weather_list_row, viewGroup, false);
            viewHolder = new ViewHolder(view); // Finds the views that needs population
            view.setTag(viewHolder); // For further use of the views

        }else{
            viewHolder = (ViewHolder) view.getTag(); // If the view exists it has the ViewHolder
        }

        //Get the associated CityWeatherData
        CityWeatherData current = (CityWeatherData) getItem(index);


        //Populate the views with the necessary information
        double temp = current.main.temp+ Globals.WEATHER_KELVIN_TO_CELSIUS;
        String sTemp = Integer.toString((int) Math.ceil(temp)) +" \u00b0C";
        viewHolder.txtCityName.setText(current.name);
        viewHolder.txtTemp.setText(sTemp);
        int id = context.getResources().getIdentifier("weather_icon_" + current.weather.get(0).icon, "drawable", context.getPackageName());;
        viewHolder.imgWeather.setImageResource(id);

        //Picasso.get().load("http://openweathermap.org/img/w/" + current.weather.get(0).icon +".png").into(viewHolder.imgWeather);
        return view;
    }



    @Override
    public Object getItem(int index){
        if(cityWeatherDataList != null)
            return cityWeatherDataList.get(index);

        return null;

    }

    @Override
    public long getItemId(int index){
        return index;
    }

    @Override
    public int getCount(){
        if(cityWeatherDataList != null)
            return cityWeatherDataList.size();

        return 0;

    }


}
