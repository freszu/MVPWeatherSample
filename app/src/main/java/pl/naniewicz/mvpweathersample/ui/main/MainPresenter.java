package pl.naniewicz.mvpweathersample.ui.main;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.EditText;

import com.google.android.gms.location.LocationRequest;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.data.remote.WeatherResponseApiException;
import pl.naniewicz.mvpweathersample.ui.base.BasePresenter;
import pl.naniewicz.mvpweathersample.util.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

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
                        .filter(charSequence -> charSequence.length() > 0)
                        .doOnNext(charSequence -> getMvpView().setRefreshingIndicator(true))
                        .debounce(DEBOUNCE_MILLISECONDS, TimeUnit.MILLISECONDS)
                        .switchMap(charSequence ->
                                mDataManager.getWeatherWithObservable(charSequence.toString())
                                        .compose(RxUtil.applySchedulers())
                                        .onErrorResumeNext(throwable -> {
                                            handleError(throwable);
                                            return Observable.empty();
                                        }))
                        .subscribe(this::displayWeatherResponse));
    }

    public void startLocationService(Activity activity) {
        getMvpView().dismissNoLocationPermissionSnackbar();
        if (getMvpView().hasLocationPermission()) {
            subscribeToLocationChanges(activity);
        } else {
            getMvpView().compatRequestFineLocationPermission(FINE_LOCATION_PERMISSION_REQUEST_CODE);
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

    public void stopLocationService() {
        if (mLocationSubscription != null && !mLocationSubscription.isUnsubscribed()) {
            mLocationSubscription.unsubscribe();
        }
    }

    public void handlePermissionResult(Activity activity, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST_CODE: {
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
