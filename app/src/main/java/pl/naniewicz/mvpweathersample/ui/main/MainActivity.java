package pl.naniewicz.mvpweathersample.ui.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;
import pl.naniewicz.mvpweathersample.R;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import pl.naniewicz.mvpweathersample.ui.base.BaseActivity;
import pl.naniewicz.mvpweathersample.util.AddressBuilderUtil;
import pl.naniewicz.mvpweathersample.util.StringFormatterUtil;

public class MainActivity extends BaseActivity implements MainMvpView {

    private MainPresenter mMainPresenter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
        setupMainPresenter();
        mMainPresenter.subscribeEditText(mEditTextLocation);
        mFAB.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.startGpsService(this);
    }

    private void setupMainPresenter() {
        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.stopGpsService();
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

    @Override
    public void setRefreshingIndicator(boolean state) {
        if (state) {
            mTextStatus.setText(getString(R.string.loading));
        } else {
            mTextStatus.setText(null);
        }
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
    public void showLocationFab() {
        if (!mFAB.isShown()) {
            mFAB.show();
        }
    }
}