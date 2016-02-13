package pl.naniewicz.mvpweathersample.data.remote;

/**
 * Created by Rafał Naniewicz on 13.02.2016.
 */
public class WeatherResponseApiException extends Exception {
    public WeatherResponseApiException(String detailMessage) {
        super(detailMessage);
    }
}
