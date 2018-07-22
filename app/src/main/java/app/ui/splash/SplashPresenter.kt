package app.ui.splash

import android.os.Handler
import app.data.DataManager
import app.model.USER
import app.model.UserPrefs
import app.mvpbase.BasePresenter
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class SplashPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<SplashView>() {
    override fun attachView(mvpView: SplashView) {
        super.attachView(mvpView)
        mvpView.showProgress()
        //Dummy delay to hold splash screen for 2 seconds
        Handler().postDelayed({
            when (UserPrefs.user) {
                USER.NONE -> mvpView.launchUserSelection()
                USER.VIEWER -> mvpView.launchViewerActivity()
                USER.BROADCASTER -> mvpView.launchBroadcasterActivity()
            }
            mvpView.exitSplash()
        }, 2000)
    }
}