package pl.naniewicz.mvpweathersample.data;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import pl.naniewicz.mvpweathersample.data.local.gms.ApiClientObservable;
import pl.naniewicz.mvpweathersample.data.local.gms.LocationObservable;
import pl.naniewicz.mvpweathersample.data.local.gms.PendingResultObservable;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.WeatherResponseApiException;
import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapApiManager;
import pl.naniewicz.mvpweathersample.util.GMSUtil;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class DataManager {

    private static DataManager sInstance;

    private final OpenWeatherMapApiManager mOpenWeatherMapApiManager;

    public static DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }

    private DataManager() {
        mOpenWeatherMapApiManager = OpenWeatherMapApiManager.getInstance();
    }

    public Observable<WeatherResponse> getWeatherWithObservable(String cityName) {
        return mOpenWeatherMapApiManager.getWeatherWithObservable(cityName)
                .flatMap(this::handleWeatherResponse);
    }

    public Observable<WeatherResponse> getWeatherWithObservable(double latitude, double longitude) {
        return mOpenWeatherMapApiManager.getWeatherWithObservable(latitude, longitude)
                .flatMap(this::handleWeatherResponse);
    }

    private Observable<WeatherResponse> handleWeatherResponse(WeatherResponse weatherResponse) {
        if (weatherResponse.getCode() == 200) {
            return Observable.just(weatherResponse);
        } else {
            return Observable.error(new WeatherResponseApiException(weatherResponse.getMessage()));
        }
    }

    public Observable<Location> getDeviceLocationWithSettingsCheck(Activity callingActivity, LocationRequest locationRequest) {

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();

        return checkLocationSettings(callingActivity, locationSettingsRequest)
                .flatMap(locationSettingsResult ->
                        LocationObservable.createObservable(callingActivity, locationRequest));
    }

    private Observable<LocationSettingsResult> checkLocationSettings(Activity callingActivity, LocationSettingsRequest locationSettingsRequest) {
        return ApiClientObservable.create(callingActivity, LocationServices.API)
                .flatMap(googleApiClient ->
                        PendingResultObservable.create(
                                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                                        locationSettingsRequest)))
                .doOnNext(locationSettingsResult -> GMSUtil.handleLocationSettingsResult(
                        locationSettingsResult,
                        callingActivity));
    }

}
