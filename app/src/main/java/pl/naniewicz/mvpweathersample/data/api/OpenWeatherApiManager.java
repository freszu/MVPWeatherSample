package pl.naniewicz.mvpweathersample.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.naniewicz.mvpweathersample.Constants;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class OpenWeatherApiManager {
    private static final String METRIC_UNITS = "metric";

    private static OpenWeatherApiManager sInstance;

    private OpenWeatherMapService mOpenWeatherMapService;

    public static OpenWeatherApiManager getInstance() {
        if (sInstance == null) {
            sInstance = new OpenWeatherApiManager();
        }
        return sInstance;
    }

    private OpenWeatherApiManager() {
        mOpenWeatherMapService = getRetrofit().create(OpenWeatherMapService.class);
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    public Observable<WeatherResponse> getWeatherWithObservable(String cityName) {
        return mOpenWeatherMapService.getCurrentWeatherWithObservable(cityName,
                METRIC_UNITS,
                Constants.API_KEY);
    }

    public Observable<WeatherResponse> getWeatherWithObservable(double latitude, double longitude) {
        return mOpenWeatherMapService.getCurrentWeatherWithObservable(latitude,
                longitude,
                METRIC_UNITS,
                Constants.API_KEY);
    }


}
