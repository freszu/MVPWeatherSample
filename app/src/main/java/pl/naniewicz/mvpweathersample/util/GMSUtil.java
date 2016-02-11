package pl.naniewicz.mvpweathersample.util;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Rafał Naniewicz on 11.02.2016.
 */
public final class GMSUtil {

    private GMSUtil() {
        throw new AssertionError();
    }

    public static void handleLocationSettingsResult(LocationSettingsResult locationSettingsResult,
                                                    Activity callingActivity) {
        Status status = locationSettingsResult.getStatus();
        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
            try {
                status.startResolutionForResult(callingActivity, 1000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }
}
