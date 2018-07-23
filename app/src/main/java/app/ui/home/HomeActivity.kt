package app.ui.home

import android.os.Bundle
import app.mvpbase.MvpBaseActivity
import app.ui.mvptemplate.HomePresenter
import app.ui.mvptemplate.HomeView
import io.agora.religionapp.R
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class HomeActivity : MvpBaseActivity(), HomeView {

    @Inject
    lateinit var presenter: HomePresenter

    override val layout: Int
        get() = R.layout.activity_home

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