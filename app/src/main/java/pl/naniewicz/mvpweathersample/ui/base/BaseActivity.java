package pl.naniewicz.mvpweathersample.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import pl.naniewicz.mvpweathersample.MvpWeatherSampleApplication;
import pl.naniewicz.mvpweathersample.injection.component.ActivityComponent;
import pl.naniewicz.mvpweathersample.injection.component.DaggerActivityComponent;
import pl.naniewicz.mvpweathersample.injection.module.ActivityModule;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(MvpWeatherSampleApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

}
