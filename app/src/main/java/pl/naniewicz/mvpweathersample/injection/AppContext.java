package pl.naniewicz.mvpweathersample.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Rafal on 2016-02-18.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface AppContext {

}
