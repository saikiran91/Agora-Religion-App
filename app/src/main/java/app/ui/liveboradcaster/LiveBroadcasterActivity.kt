package app.ui.liveboradcaster

import android.os.Bundle
import app.mvpbase.MvpBaseActivity
import io.agora.religionapp.R
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class LiveBroadcasterActivity : MvpBaseActivity(), LiveBroadcasterView {

    @Inject
    lateinit var presenter: LiveBroadcasterPresenter

    override val layout: Int
        get() = R.layout.activity_live_broadcaster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        presenter.attachView(this)

    }

    override fun showProgress() {

    }

    override fun hideProgress() {
    }

    override fun showError(message: String) {
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}