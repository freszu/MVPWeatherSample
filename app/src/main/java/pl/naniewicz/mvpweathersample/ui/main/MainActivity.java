package pl.naniewicz.mvpweathersample.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import pl.naniewicz.mvpweathersample.R;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.ui.base.BaseActivity;
import pl.naniewicz.mvpweathersample.util.AddressBuilderUtil;
import pl.naniewicz.mvpweathersample.util.StringFormatterUtil;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final int CHECK_LOCATION_SETTINGS_REQUEST_CODE = 1;
    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 2;

    @Inject MainPresenter mMainPresenter;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.edit_text_location) EditText mEditTextLocation;
    @Bind(R.id.text_status) TextView mTextStatus;
    @Bind(R.id.image_weather_icon) ImageView mImageWeatherIcon;
    @Bind(R.id.text_place) TextView mTextPlace;
    @Bind(R.id.text_temperature) TextView mTextTemperature;
    @Bind(R.id.text_temperature_max) TextView mTextTemperatureMax;
    @Bind(R.id.text_temperature_min) TextView mTextTemperatureMin;
    @Bind(R.id.text_description) TextView mTextDescription;
    @Bind(R.id.fab_gps_based_forecast) FloatingActionButton mFAB;
    private Snackbar mWarningSnackbar;

    private boolean mIsLocationSettingsStatusForResultCalled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.subscribeEditText(mEditTextLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFAB.hide();
        mMainPresenter.startLocationService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0) {
            mMainPresenter.handleFineLocationPermissionResult(grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_LOCATION_SETTINGS_REQUEST_CODE) {
            mIsLocationSettingsStatusForResultCalled = false;
            mMainPresenter.handleLocationSettingsDialogResult(resultCode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainPresenter.stopLocationService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab_gps_based_forecast)
    public void onFabGpsClick() {
        mMainPresenter.loadGPSBasedForecast();
    }

    /* MVP Methods */

    @Override
    public boolean hasFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void setRefreshingIndicator(boolean state) {
        mTextStatus.setText(state ? getString(R.string.loading) : null);
    }

    @Override
    public void showWeather(WeatherResponse weatherResponse) {
        Picasso.with(this)
                .load(AddressBuilderUtil.getIconAddress(weatherResponse.getWeather().get(0).getIcon()))
                .fit()
                .centerCrop()
                .into(mImageWeatherIcon);
        mTextPlace.setText(StringFormatterUtil.getPlace(weatherResponse));
        mTextTemperature.setText(StringFormatterUtil.getTemperature(weatherResponse));
        mTextTemperatureMax.setText(StringFormatterUtil.getMaxTemperature(weatherResponse));
        mTextTemperatureMin.setText(StringFormatterUtil.getMinTemperature(weatherResponse));
        mTextDescription.setText(StringFormatterUtil.getDescription(weatherResponse));
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showApiError(String apiError) {
        mTextStatus.setText(apiError);
    }

    @Override
    public void showLocationFab() {
        if (!mFAB.isShown()) {
            mFAB.show();
        }
    }

    @Override
    public void compatRequestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void showNoFineLocationPermissionWarning() {
        mWarningSnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                R.string.warning_no_fine_location_permission,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, v -> goToAppSettings());
        mWarningSnackbar.show();
    }

    private void goToAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void dismissWarning() {
        if (mWarningSnackbar != null && mWarningSnackbar.isShown()) {
            mWarningSnackbar.dismiss();
        }
    }

    @Override
    public void onUserResolvableLocationSettings(Status status) {
        try {
            status.startResolutionForResult(this, CHECK_LOCATION_SETTINGS_REQUEST_CODE);
            mIsLocationSettingsStatusForResultCalled = true;
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLocationSettingsWarning() {
        mWarningSnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                R.string.warning_inadequate_location_settings,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.change_settings, v -> mMainPresenter.checkLocationSettings());
        mWarningSnackbar.show();
    }

    @Override
    public void onGmsConnectionResultResolutionRequired(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, -1);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGmsConnectionResultNoResolution(int errorCode) {
        GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, 0).show();
    }

    @Override
    public boolean isLocationSettingsStatusDialogCalled() {
        return mIsLocationSettingsStatusForResultCalled;
    }
}