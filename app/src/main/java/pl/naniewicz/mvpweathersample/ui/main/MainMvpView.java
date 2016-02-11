package pl.naniewicz.mvpweathersample.ui.main;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.ui.base.MvpView;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public interface MainMvpView extends MvpView {

    void compatRequestPermissions(int requestCode, String... permissions);

    void setRefreshingIndicator(boolean state);

    void showWeather(WeatherResponse weatherResponse);

    void showError(String locationString);

    void showApiError(String apiError);

    void showLocationFab();

    void showNoLocationPermissionSnackbar();
}
