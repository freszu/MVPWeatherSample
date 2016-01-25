package pl.naniewicz.mvpweathersample.util;

import pl.naniewicz.mvpweathersample.data.model.WeatherResponse;

/**
 * Created by Rafał Naniewicz on 24.01.2016.
 */
public class StringFormatterUtil {

    public static String getPlace(WeatherResponse weatherResponse) {
        StringBuilder stringBuilder = new StringBuilder(weatherResponse.getName());
        if (weatherResponse.getSys() != null) {
            stringBuilder.append(", ").append(weatherResponse.getSys().getCountry());
        }
        return stringBuilder.toString();
    }

    public static String getTemperature(WeatherResponse weatherResponse) {

        if (weatherResponse.getMain() != null &&
                weatherResponse.getMain().getTemp() != null) {
            return getTemperatureString(weatherResponse.getMain().getTemp());
        }
        return null;
    }

    public static String getMaxTemperature(WeatherResponse weatherResponse) {

        if (weatherResponse.getMain() != null &&
                weatherResponse.getMain().getTempMax() != null) {
            return getTemperatureString(weatherResponse.getMain().getTempMax());
        }
        return null;
    }

    public static String getMinTemperature(WeatherResponse weatherResponse) {

        if (weatherResponse.getMain() != null &&
                weatherResponse.getMain().getTempMin() != null) {
            return getTemperatureString(weatherResponse.getMain().getTempMin());
        }
        return null;
    }

    private static String getTemperatureString(double temperature) {
        return String.valueOf(Math.round(temperature)).concat(" °C");
    }

    public static String getDescription(WeatherResponse weatherResponse) {
        if (weatherResponse.getWeather() != null &&
                weatherResponse.getWeather().get(0) != null) {
            return weatherResponse.getWeather().get(0).getDescription();
        }
        return null;
    }
}
