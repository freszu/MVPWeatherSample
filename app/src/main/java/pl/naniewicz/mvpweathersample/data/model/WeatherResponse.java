package pl.naniewicz.mvpweathersample.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class WeatherResponse {

    @SerializedName("weather") private ArrayList<Weather> mWeather;

    @SerializedName("main") private Main mMain;

    @SerializedName("sys") private Sys mSys;

    @SerializedName("name") private String mName;

    @SerializedName("cod") private int mCode;

    @SerializedName("message") private String mMessage;

    public ArrayList<Weather> getWeather() {
        return mWeather;
    }

    public Main getMain() {
        return mMain;
    }

    public Sys getSys() {
        return mSys;
    }

    public String getName() {
        return mName;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
