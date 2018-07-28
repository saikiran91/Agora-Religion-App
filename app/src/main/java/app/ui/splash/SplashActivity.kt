package app.ui.splash

import android.content.Intent
import android.os.Bundle
import app.mvpbase.MvpBaseActivity
import app.ui.home.HomeActivity
import app.ui.userselection.UserSelectionActivity
import app.util.hide
import app.util.show
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_splash.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class SplashActivity : MvpBaseActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override val layout: Int
        get() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        presenter.attachView(this)
    }

    override fun showProgress() {
        progress_bar.show()
    }

    override fun hideProgress() {
        progress_bar.hide()
    }

    override fun launchUserSelection() {
        startActivity(Intent(this, UserSelectionActivity::class.java))

    }

    override fun launchBroadcasterActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun launchViewerActivity() {
        startActivity(Intent(this, app.ui.viewer.home.HomeActivity::class.java))

    }

    override fun exitSplash() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}