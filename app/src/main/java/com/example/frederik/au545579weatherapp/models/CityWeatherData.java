
package com.example.frederik.au545579weatherapp.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Generated with http://www.jsonschema2pojo.org/ From the example return data from open weather api. https://openweathermap.org/current
public class CityWeatherData {

    @SerializedName("coord")
    @Expose
    public Coord coord;
    @SerializedName("weather")
    @Expose
    public List<Weather> weather = null;
    @SerializedName("base")
    @Expose
    public String base;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("clouds")
    @Expose
    public Clouds clouds;
    @SerializedName("rain")
    @Expose
    public Rain rain;
    @SerializedName("dt")
    @Expose
    public Integer dt;
    @SerializedName("sys")
    @Expose
    public Sys sys;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("cod")
    @Expose
    public Integer cod;

    //https://stackoverflow.com/questions/185937/overriding-the-java-equals-method-quirk
    //Comparing CityWeatherData equality on city name
    @Override
    public boolean equals(Object other){
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (!(other instanceof CityWeatherData))
            return false;


        CityWeatherData otherCasted = (CityWeatherData)other;
            return this.name.equals(otherCasted.name);
    }

}
