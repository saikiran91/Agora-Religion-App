package app.ui.mvptemplate

import android.os.Bundle
import app.mvpbase.MvpBaseActivity
import app.ui.ongoingbroadcaster.OngoingBroadcasterView
import io.agora.religionapp.R
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class TemplateActivity : MvpBaseActivity(), TemplateView {

    @Inject
    lateinit var presenter: TemplatePresenter

    override val layout: Int
        get() = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activityComponent().inject(this) //TODO Add the activity in ActivityComponent
//        presenter.attachView(this)

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