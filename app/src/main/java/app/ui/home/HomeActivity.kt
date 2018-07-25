package app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import app.mvpbase.MvpBaseActivity
import app.ui.addevent.AddEventActivity
import app.ui.mvptemplate.TemplatePresenter
import app.ui.mvptemplate.TemplateView
import io.agora.religionapp.R
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class HomeActivity : MvpBaseActivity(), TemplateView {

    @Inject
    lateinit var presenter: TemplatePresenter

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

    fun onAddEventClicked(view: View) {
        startActivity(Intent(this, AddEventActivity::class.java))

    }
}