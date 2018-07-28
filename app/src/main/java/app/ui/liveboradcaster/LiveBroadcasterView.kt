package app.ui.liveboradcaster

import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface LiveBroadcasterView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
}