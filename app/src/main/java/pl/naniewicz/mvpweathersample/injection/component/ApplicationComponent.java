package pl.naniewicz.mvpweathersample.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.naniewicz.mvpweathersample.data.DataManager;
import pl.naniewicz.mvpweathersample.injection.AppContext;
import pl.naniewicz.mvpweathersample.injection.module.ApplicationModule;

/**
 * Created by Rafal on 2016-02-18.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @AppContext
    Context context();

    Application application();

    DataManager dataManager();
}
