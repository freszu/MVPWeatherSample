package pl.naniewicz.mvpweathersample.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.naniewicz.mvpweathersample.BuildConfig;
import pl.naniewicz.mvpweathersample.Constants;
import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public interface OpenWeatherMapService {

    @GET("data/2.5/weather")
    Observable<WeatherResponse> getCurrentWeatherWithObservable(
            @Query("q") String cityName,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("data/2.5/weather")
    Observable<WeatherResponse> getCurrentWeatherWithObservable(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    class Factory {

        public static OpenWeatherMapService createAsrService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(OpenWeatherMapService.class);
        }

        private static OkHttpClient getOkHttpClient() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);
            return new OkHttpClient.Builder().addInterceptor(interceptor).build();
        }
    }
}
