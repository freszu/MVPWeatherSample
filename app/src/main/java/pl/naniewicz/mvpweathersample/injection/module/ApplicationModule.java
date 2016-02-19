package pl.naniewicz.mvpweathersample.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.naniewicz.mvpweathersample.data.remote.OpenWeatherMapService;
import pl.naniewicz.mvpweathersample.injection.AppContext;

/**
 * Created by Rafal on 2016-02-18.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @AppContext
    Context providesContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    OpenWeatherMapService provideAsrService() {
        return OpenWeatherMapService.Factory.createAsrService();
    }
}
