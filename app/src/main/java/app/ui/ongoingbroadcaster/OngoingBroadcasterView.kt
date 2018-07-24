package app.ui.ongoingbroadcaster

import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface OngoingBroadcasterView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
}