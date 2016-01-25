package pl.naniewicz.mvpweathersample.data;

import pl.naniewicz.mvpweathersample.data.api.OpenWeatherApiManager;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class DataManager {

    private OpenWeatherApiManager mOpenWeatherApiManager;

    public DataManager() {
        mOpenWeatherApiManager = OpenWeatherApiManager.getInstance();
    }

    public Observable<WeatherResponse> getWeatherWithObservable(String cityName) {
        return mOpenWeatherApiManager.getWeatherWithObservable(cityName);
    }

    public Observable<WeatherResponse> getWeatherWithObservable(double latitude, double longitude) {
        return mOpenWeatherApiManager.getWeatherWithObservable(latitude, longitude);
    }
}
