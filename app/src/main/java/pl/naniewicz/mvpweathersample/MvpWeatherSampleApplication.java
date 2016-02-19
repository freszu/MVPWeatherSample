package pl.naniewicz.mvpweathersample;

import android.app.Application;
import android.content.Context;

import pl.naniewicz.mvpweathersample.injection.component.ApplicationComponent;
import pl.naniewicz.mvpweathersample.injection.module.ApplicationModule;
import pl.naniewicz.mvpweathersample.injection.component.DaggerApplicationComponent;
/**
 * Created by Rafal on 2016-02-18.
 */
public class MvpWeatherSampleApplication extends Application {

    ApplicationComponent mApplicationComponent;

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    public static MvpWeatherSampleApplication get(Context context) {
        return (MvpWeatherSampleApplication) context.getApplicationContext();
    }
}

