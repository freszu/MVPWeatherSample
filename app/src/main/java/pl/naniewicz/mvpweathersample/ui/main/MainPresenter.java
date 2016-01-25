package pl.naniewicz.mvpweathersample.ui.main;


import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.ui.base.BasePresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public class MainPresenter extends BasePresenter<MainMvpView> {

    private static final int DEBOUNCE_MILLISECONDS = 500;

    private DataManager mDataManager;
    private Subscription mSubscription;

    public MainPresenter() {
        mDataManager = new DataManager();
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public void subscribeEditText(EditText editTextCity) {
        mSubscription = RxTextView.textChanges(editTextCity)
                .debounce(DEBOUNCE_MILLISECONDS, TimeUnit.MILLISECONDS)
                .filter(charSequence -> charSequence.length() > 0)
                .switchMap(charSequence -> mDataManager.getWeatherWithObservable(charSequence.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.newThread()))
                .onErrorResumeNext(Observable.empty())
                .subscribe(
                        openWeatherMapResponse -> {
                            if (openWeatherMapResponse.getCode() == 200) {
                                getMvpView().showWeather(openWeatherMapResponse);
                                getMvpView().setRefreshingIndicator(false);
                            } else {
                                getMvpView().showError(openWeatherMapResponse.getMessage());
                            }
                        });
    }

    public void startGpsService() {
        //// TODO: 25.01.2016 implement gps location
    }

    public void loadGPSBasedForecast() {
        //// TODO: 26.01.2016 implement gps location
    }
}
