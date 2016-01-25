package pl.naniewicz.mvpweathersample.ui.base;

import android.widget.EditText;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public abstract class BasePresenter<T extends MvpView> implements Presenter<T> {

    private T mMvpView;

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    @Override
    public boolean isViewAttached() {
        return mMvpView != null;
    }

    @Override
    public T getMvpView() {
        return mMvpView;
    }
}
