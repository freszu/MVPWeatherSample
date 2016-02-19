package pl.naniewicz.mvpweathersample.data;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.naniewicz.mvpweathersample.Constants;
import pl.naniewicz.mvpweathersample.data.local.gms.GmsLocationHelper;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapService;
import pl.naniewicz.mvpweathersample.data.remote.WeatherResponseApiException;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
@Singleton
public class DataManager {

    private OpenWeatherMapService mOpenWeatherMapService;
    private GmsLocationHelper mGmsLocationHelper;

    @Inject
    public DataManager(OpenWeatherMapService openWeatherMapService, GmsLocationHelper gmsLocationHelper) {
        mOpenWeatherMapService = openWeatherMapService;
        mGmsLocationHelper = gmsLocationHelper;
    }

    public Observable<WeatherResponse> getCurrentWeatherWithObservable(String cityName) {
        return mOpenWeatherMapService.getCurrentWeatherWithObservable(cityName, Constants.UNITS, Constants.API_KEY)
                .flatMap(this::handleWeatherResponse);
    }

    public Observable<WeatherResponse> getCurrentWeatherWithObservable(double latitude, double longitude) {
        return mOpenWeatherMapService.getCurrentWeatherWithObservable(latitude, longitude, Constants.UNITS, Constants.API_KEY)
                .flatMap(this::handleWeatherResponse);
    }

    private Observable<WeatherResponse> handleWeatherResponse(WeatherResponse weatherResponse) {
        if (weatherResponse.getCode() == 200) {
            return Observable.just(weatherResponse);
        } else {
            return Observable.error(new WeatherResponseApiException(weatherResponse.getMessage()));
        }
    }

    public Observable<Location> getDeviceLocation(LocationRequest locationRequest) {
        return mGmsLocationHelper.getDeviceLocation(locationRequest);
    }

    public Observable<LocationSettingsResult> checkLocationSettings(LocationRequest locationRequest) {
        return mGmsLocationHelper.checkLocationSettings(locationRequest);
    }

}
