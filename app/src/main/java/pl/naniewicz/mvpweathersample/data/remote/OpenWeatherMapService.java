package pl.naniewicz.mvpweathersample.data.remote;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;
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
}
