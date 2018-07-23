package app.ui.home

import app.model.USER
import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface HomeView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
}