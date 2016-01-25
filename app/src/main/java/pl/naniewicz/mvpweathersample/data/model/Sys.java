package pl.naniewicz.mvpweathersample.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class Sys {

    @SerializedName("country") private String mCountry;

    public String getCountry() {
        return mCountry;
    }
}
