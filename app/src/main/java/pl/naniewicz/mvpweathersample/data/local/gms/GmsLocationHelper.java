package pl.naniewicz.mvpweathersample.data.local.gms;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.naniewicz.mvpweathersample.injection.AppContext;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 15.02.2016.
 */

@Singleton
public class GmsLocationHelper {

    public final static LocationRequest APP_LOCATION_REQUEST = getLocationRequest();
    private static final int LOCATION_UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS = LOCATION_UPDATE_INTERVAL_MILLISECONDS / 2;
    private static final int LOCATION_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    private Context mContext;

    @Inject
    public GmsLocationHelper(@AppContext Context context) {
        mContext = context;
    }

    public Observable<Location> getDeviceLocation(LocationRequest locationRequest) {
        return LocationObservable.createObservable(mContext, locationRequest);
    }

    public Observable<LocationSettingsResult> checkLocationSettings(LocationRequest locationRequest) {
        return ApiClientObservable.create(mContext, LocationServices.API)
                .flatMap(googleApiClient -> PendingResultObservable.create(
                        LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                                getLocationSettingsRequest(locationRequest))));
    }

    private LocationSettingsRequest getLocationSettingsRequest(LocationRequest locationRequest) {
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build();
    }

    private static LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setFastestInterval(LOCATION_FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .setInterval(LOCATION_UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(LOCATION_ACCURACY);
    }
}
