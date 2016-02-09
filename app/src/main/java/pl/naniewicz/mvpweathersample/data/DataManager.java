package pl.naniewicz.mvpweathersample.data;

import android.content.Context;
import android.location.Location;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapApiManager;
import pl.naniewicz.mvpweathersample.util.GoogleApiObservable;
import pl.naniewicz.mvpweathersample.util.LocationObservable;
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

    public Observable<Location> getDeviceLocation(Context context,
                                                  long fastestUpdateIntervalMilliSecs,
                                                  long updateIntervalMilliSecs,
                                                  int locationRequestPriority) {
        return Observable.create(new GoogleApiObservable(context))
                .flatMap(googleApiClient -> Observable.create(new LocationObservable(
                        googleApiClient,
                        fastestUpdateIntervalMilliSecs,
                        updateIntervalMilliSecs,
                        locationRequestPriority)));

    }

}
