package pl.naniewicz.mvpweathersample.util;

import android.util.Log;

import pl.naniewicz.mvpweathersample.BuildConfig;


/**
 * Created by Rafal on 2016-02-18.
 */
public final class LogUtil {

    private LogUtil() {
        throw new AssertionError();
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void wtf(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }
}
