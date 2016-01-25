package pl.naniewicz.mvpweathersample.data;

import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapApiManager;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class DataManager {

    private OpenWeatherMapApiManager mOpenWeatherMapApiManager;

    public DataManager() {
        mOpenWeatherMapApiManager = OpenWeatherMapApiManager.getInstance();
    }

    public Observable<WeatherResponse> getWeatherWithObservable(String cityName) {
        return mOpenWeatherMapApiManager.getWeatherWithObservable(cityName);
    }

    public Observable<WeatherResponse> getWeatherWithObservable(double latitude, double longitude) {
        return mOpenWeatherMapApiManager.getWeatherWithObservable(latitude, longitude);
    }
}
