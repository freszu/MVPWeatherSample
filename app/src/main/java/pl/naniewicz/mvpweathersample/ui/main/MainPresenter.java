package pl.naniewicz.mvpweathersample.ui.main;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;

import com.google.android.gms.location.LocationRequest;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.ui.base.BasePresenter;
import pl.naniewicz.mvpweathersample.util.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION = 1;

    private static final int DEBOUNCE_MILLISECONDS = 500;
    private static final int UPDATE_INTERVAL_MILLISECONDS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2;
    private static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private Subscription mLocationSubscription;
    private Location mLatestLocation;

    public MainPresenter() {
        mDataManager = DataManager.getInstance();
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
    }

    public void subscribeEditText(EditText editTextCity) {
        mSubscriptions.add(
                RxTextView.textChanges(editTextCity)
                        .debounce(DEBOUNCE_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .filter(charSequence -> charSequence.length() > 0)
                        .switchMap(charSequence ->
                                mDataManager.getWeatherWithObservable(charSequence.toString())
                                        .compose(RxUtil.applySchedulers())
                                        .onErrorResumeNext(throwable -> {
                                            getMvpView().showError(throwable.getMessage());
                                            return Observable.empty();
                                        }))
                        .subscribe(this::handleWeatherResponse));
    }

    public void startLocationService(Activity activity) {
        if (hasLocationPermission(activity)) {
            subscribeToLocationChanges(activity);
        } else {
            getMvpView().compatRequestPermissions(PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void subscribeToLocationChanges(Activity activity) {
        mLocationSubscription =
                mDataManager.getDeviceLocationWithSettingsCheck(activity, getLocationRequest())
                        .subscribe(
                                location -> {
                                    getMvpView().showLocationFab();
                                    mLatestLocation = location;
                                },
                                throwable -> getMvpView().showError(throwable.getMessage())
                        );
    }

    private LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .setInterval(UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LOCATION_ACCURACY);
    }

    private boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void stopGpsService() {
        if (mLocationSubscription != null && !mLocationSubscription.isUnsubscribed()) {
            mLocationSubscription.unsubscribe();
        }
    }

    public void handlePermissionResult(Activity activity, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    subscribeToLocationChanges(activity);
                } else {
                    getMvpView().showNoLocationPermissionSnackbar();
                }
                break;
            }
        }
    }

    public void loadGPSBasedForecast() {
        getMvpView().setRefreshingIndicator(true);
        mSubscriptions.add(mDataManager.getWeatherWithObservable(
                mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude())
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        this::handleWeatherResponse,
                        throwable -> getMvpView().showError(throwable.getMessage())));
    }

    private void handleWeatherResponse(WeatherResponse weatherResponse) {
        if (weatherResponse.getCode() == 200) {
            getMvpView().showWeather(weatherResponse);
            getMvpView().setRefreshingIndicator(false);
        } else {
            getMvpView().showApiError(weatherResponse.getMessage());
        }
    }

}
