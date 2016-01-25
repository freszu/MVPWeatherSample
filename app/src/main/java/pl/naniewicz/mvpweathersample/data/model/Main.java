package pl.naniewicz.mvpweathersample.data.model;

import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp") private Double mTemp;

    @SerializedName("temp_min") private Double mTempMin;

    @SerializedName("temp_max") private Double mTempMax;

    public Double getTemp() {
        return mTemp;
    }

    public Double getTempMin() {
        return mTempMin;
    }

    public Double getTempMax() {
        return mTempMax;
    }
}
