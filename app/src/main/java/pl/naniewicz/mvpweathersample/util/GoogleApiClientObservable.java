package pl.naniewicz.mvpweathersample.util;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rafał Naniewicz on 07.02.2016.
 */
public class GoogleApiClientObservable implements Observable.OnSubscribe<GoogleApiClient>, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final GoogleApiClient mGoogleApiClient;
    private Subscriber<? super GoogleApiClient> mSubscriber;

    public GoogleApiClientObservable(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void call(Subscriber<? super GoogleApiClient> subscriber) {
        mSubscriber = subscriber;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onError(new GoogleApiConnectionFailedException(connectionResult.toString()));
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!mSubscriber.isUnsubscribed()) {
            mSubscriber.onNext(mGoogleApiClient);
            mSubscriber.onCompleted();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public class GoogleApiConnectionFailedException extends Exception {

        public GoogleApiConnectionFailedException(String detailMessage) {
            super(detailMessage);
        }
    }
}
