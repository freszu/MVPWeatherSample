package pl.naniewicz.mvpweathersample.data;

import android.content.Context;
import android.location.Location;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapApiManager;
import pl.naniewicz.mvpweathersample.util.GoogleApiClientObservable;
import pl.naniewicz.mvpweathersample.util.LocationObsevable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        return Observable.create(new GoogleApiClientObservable(context))
                .flatMap(googleApiClient -> Observable.create(new LocationObsevable(
                        googleApiClient,
                        fastestUpdateIntervalMilliSecs,
                        updateIntervalMilliSecs,
                        locationRequestPriority)));

    }

}
