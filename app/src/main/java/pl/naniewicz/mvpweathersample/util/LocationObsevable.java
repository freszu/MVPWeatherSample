package pl.naniewicz.mvpweathersample.util;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by Rafał Naniewicz on 07.02.2016.
 */
public class LocationObsevable implements LocationListener, Action0, Observable.OnSubscribe<Location> {

    private final LocationRequest mLocationRequest;
    private final GoogleApiClient mGoogleApiClient;
    private Subscriber<? super Location> mSubscriber;

    public LocationObsevable(GoogleApiClient googleApiClient,
                             long fastestUpdateIntervalMilliSecs,
                             long updateIntervalMilliSecs,
                             int locationRequestPriority) {
        mGoogleApiClient = googleApiClient;
        mLocationRequest = new LocationRequest()
                .setFastestInterval(fastestUpdateIntervalMilliSecs)
                .setInterval(updateIntervalMilliSecs)
                .setPriority(locationRequestPriority);

    }

    @Override
    public void call(Subscriber<? super Location> subscriber) {
        mSubscriber = subscriber;
        mSubscriber.add(Subscriptions.create(this));
        mSubscriber.onNext(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void call() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(location);
        }
    }


}
