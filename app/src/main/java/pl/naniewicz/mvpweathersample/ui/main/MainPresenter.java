package pl.naniewicz.mvpweathersample.ui.main;


import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.location.LocationRequest;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.ui.base.BasePresenter;
import pl.naniewicz.mvpweathersample.util.GoogleApiObservable;
import pl.naniewicz.mvpweathersample.util.RxUtil;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final int DEBOUNCE_MILLISECONDS = 500;
    private static final int UPDATE_INTERVAL_MILLISECONDS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = UPDATE_INTERVAL_MILLISECONDS / 2;


    private DataManager mDataManager;
    private CompositeSubscription mSubscriptions;
    private Subscription mLocationSubscription;
    private Location mLatestLocation;

    public MainPresenter() {
        mDataManager = new DataManager();
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
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .unsubscribeOn(Schedulers.newThread())
                                        .onErrorResumeNext(throwable -> {
                                            getMvpView().showError(throwable.getMessage());
                                            return Observable.empty();
                                        }))
                        .subscribe(
                                openWeatherMapResponse -> {
                                    if (openWeatherMapResponse.getCode() == 200) {
                                        getMvpView().showWeather(openWeatherMapResponse);
                                        getMvpView().setRefreshingIndicator(false);
                                    } else {
                                        getMvpView().showError(openWeatherMapResponse.getMessage());
                                    }
                                }));
    }

    public void startGpsService(Context context) {
        mLocationSubscription =
                mDataManager.getDeviceLocation(context,
                        FASTEST_UPDATE_INTERVAL_MILLISECONDS,
                        UPDATE_INTERVAL_MILLISECONDS,
                        LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .subscribe(
                                location -> {
                                    getMvpView().showLocationFab();
                                    mLatestLocation = location;
                                },
                                throwable -> {
                                    getMvpView().showError(throwable.getMessage());
                                }
                        );
    }

    public void stopGpsService() {
        if (mLocationSubscription != null && !mLocationSubscription.isUnsubscribed()) {
            mLocationSubscription.unsubscribe();
        }
    }


    public void loadGPSBasedForecast() {
        getMvpView().setRefreshingIndicator(true);
        mSubscriptions.add(mDataManager.getWeatherWithObservable(mLatestLocation.getLatitude(),
                mLatestLocation.getLongitude())
                .compose(RxUtil.applySchedulers())
                .onErrorResumeNext(throwable -> {
                    getMvpView().showError(throwable.getMessage());
                    return Observable.empty();
                })
                .subscribe(
                        openWeatherMapResponse -> {
                            if (openWeatherMapResponse.getCode() == 200) {
                                getMvpView().showWeather(openWeatherMapResponse);
                                getMvpView().setRefreshingIndicator(false);
                            } else {
                                getMvpView().showError(openWeatherMapResponse.getMessage());
                            }
                        }));
    }
}
