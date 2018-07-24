package app.ui.addevent

import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface AddEventView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
}