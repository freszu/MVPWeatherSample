package pl.naniewicz.mvpweathersample.injection.component;

import dagger.Component;
import pl.naniewicz.mvpweathersample.injection.ActivityScope;
import pl.naniewicz.mvpweathersample.injection.module.ActivityModule;
import pl.naniewicz.mvpweathersample.ui.base.BaseActivity;
import pl.naniewicz.mvpweathersample.ui.main.MainActivity;


/**
 * Created by Rafal on 2016-02-18.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);
}
