package pl.naniewicz.mvpweathersample.ui.main;


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
    private DataManager mDataManager;
    private Subscription mSubscription;

    public MainPresenter() {
        mDataManager = new DataManager();
    }

    @Override public void detachView() {
        super.detachView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public void startGpsService() {
        //// TODO: 25.01.2016 implement gps location
    }

    public void loadForecast(String city) {
        //// TODO: 25.01.2016 load from gps if city==null
        getMvpView().setRefreshingIndicator(true);
        mSubscription = mDataManager.getWeatherWithObservable(city)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.newThread())
                .flatMap(WeatherResponse -> WeatherResponse.getCode() == 200 ?
                        Observable.just(WeatherResponse) :
                        Observable.error(new Throwable(WeatherResponse.getMessage())))
                .subscribe(
                        OpenWeatherMapResponse -> {
                            getMvpView().showWeather(OpenWeatherMapResponse);
                            getMvpView().setRefreshingIndicator(false);
                        },
                        throwable -> getMvpView().showError(throwable)
                );
    }
}
