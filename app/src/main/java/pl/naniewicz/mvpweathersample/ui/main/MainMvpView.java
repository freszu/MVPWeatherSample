package pl.naniewicz.mvpweathersample.ui.main;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.ui.base.MvpView;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public interface MainMvpView extends MvpView {

    boolean hasFineLocationPermission();

    void setRefreshingIndicator(boolean state);

    void showWeather(WeatherResponse weatherResponse);

    void showError(String locationString);

    void showApiError(String apiError);

    void showLocationFab();

    void compatRequestFineLocationPermission();

    void showNoFineLocationPermissionWarning();

    void dismissWarning();

    void onUserResolvableLocationSettings(Status status);

    void showLocationSettingsWarning();

    void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult);

    void onGmsConnectionResultNoResolution(int errorCode);

    boolean isLocationSettingsStatusDialogCalled();
}
