package app.ui.mvptemplate

import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface TemplateView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
}