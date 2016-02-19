package pl.naniewicz.mvpweathersample.ui.main;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.data.local.gms.ApiClientConnectionFailedException;
import pl.naniewicz.mvpweathersample.data.local.gms.GmsLocationHelper;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.WeatherResponseApiException;
import pl.naniewicz.mvpweathersample.ui.base.BasePresenter;
import pl.naniewicz.mvpweathersample.util.LogUtil;
import pl.naniewicz.mvpweathersample.util.RxUtil;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final int DEBOUNCE_MILLISECONDS = 500;

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private CompositeSubscription mLocationSubscriptions;
    private Location mLatestLocation;

    private static final String TAG = "MainPresenter";

    @Inject
    public MainPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mSubscriptions = new CompositeSubscription();
        mLocationSubscriptions = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
        mLocationSubscriptions.unsubscribe();
    }

    public void subscribeEditText(EditText editTextCity) {
        mSubscriptions.add(
                RxTextView.textChanges(editTextCity)
                        .filter(charSequence -> charSequence.length() > 0)
                        .doOnNext(charSequence -> getMvpView().setRefreshingIndicator(true))
                        .debounce(DEBOUNCE_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .switchMap(charSequence ->
                                mDataManager.getCurrentWeatherWithObservable(charSequence.toString())
                                        .compose(RxUtil.applySchedulers())
                                        .onErrorResumeNext(throwable -> {
                                            handleError(throwable);
                                            return Observable.empty();
                                        }))
                        .subscribe(this::displayWeatherResponse));
    }

    public void startLocationService() {
        getMvpView().dismissWarning();
        if (getMvpView().hasFineLocationPermission()) {
            checkLocationSettings();
        } else {
            getMvpView().compatRequestFineLocationPermission();
        }
    }

    public void handleFineLocationPermissionResult(int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings();
        } else {
            getMvpView().showNoFineLocationPermissionWarning();
        }
    }

    public void checkLocationSettings() {
        if (!getMvpView().isLocationSettingsStatusDialogCalled()) {
            mLocationSubscriptions.add(mDataManager.checkLocationSettings(GmsLocationHelper.APP_LOCATION_REQUEST)
                    .subscribe(this::handleLocationSettings,
                            this::handleGmsError));
        }
    }

    private void handleLocationSettings(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                subscribeToLocationChanges();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                getMvpView().onUserResolvableLocationSettings(status);
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                LogUtil.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public void handleLocationSettingsDialogResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            subscribeToLocationChanges();
        } else {
            getMvpView().showLocationSettingsWarning();
        }
    }

    private void handleGmsError(Throwable throwable) {
        if (throwable instanceof ApiClientConnectionFailedException) {
            ConnectionResult connectionResult =
                    ((ApiClientConnectionFailedException) throwable).getConnectionResult();
            if (connectionResult.hasResolution()) {
                getMvpView().onGmsConnectionResultResolutionRequired(connectionResult);
            } else {
                getMvpView().onGmsConnectionResultNoResolution(connectionResult.getErrorCode());
            }
        }
    }

    private void subscribeToLocationChanges() {
        mLocationSubscriptions.add(
                mDataManager.getDeviceLocation(GmsLocationHelper.APP_LOCATION_REQUEST)
                        .subscribe(
                                location -> {
                                    getMvpView().showLocationFab();
                                    mLatestLocation = location;
                                },
                                this::handleGmsError));
    }

    public void stopLocationService() {
        mLocationSubscriptions.clear();
    }

    public void loadGPSBasedForecast() {
        getMvpView().setRefreshingIndicator(true);
        mSubscriptions.add(mDataManager.getCurrentWeatherWithObservable(
                mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude())
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        this::displayWeatherResponse,
                        this::handleError));
    }

    private void displayWeatherResponse(WeatherResponse weatherResponse) {
        getMvpView().showWeather(weatherResponse);
        getMvpView().setRefreshingIndicator(false);
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof WeatherResponseApiException) {
            getMvpView().showApiError(throwable.getMessage());
        } else {
            getMvpView().showError(throwable.getMessage());
        }
    }


}
