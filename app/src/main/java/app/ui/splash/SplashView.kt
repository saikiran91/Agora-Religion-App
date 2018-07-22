package app.ui.splash

import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface SplashView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun launchUserSelection()
    fun launchBroadcasterActivity()
    fun launchViewerActivity()
    fun exitSplash()
}