package pl.naniewicz.mvpweathersample.ui.base;

/**
 * Created by Rafał Naniewicz on 22.01.2016.
 */
public interface Presenter<T extends MvpView> {

    void attachView(T mvpView);

    void detachView();

    boolean isViewAttached();

    T getMvpView();
}
