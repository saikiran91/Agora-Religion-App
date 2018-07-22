package app.mvpbase

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
open class BasePresenter<T : MvpView> : Presenter<T> {

    var mvpView: T? = null
        private set
    private val disposable = CompositeDisposable()

    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
        if (!disposable.isDisposed) {
            disposable.clear()
        }
    }

    val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addDisposable(subs: Disposable) {
        disposable.add(subs)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")

}

