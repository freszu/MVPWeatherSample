package pl.naniewicz.mvpweathersample.data.model;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("icon") private String mIcon;

    @SerializedName("description") private String mDescription;

    public String getIcon() {
        return mIcon;
    }

    public String getDescription() {
        return mDescription;
    }

}
