package app.ui.addevent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.mvpbase.MvpBaseActivity
import app.util.*
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_add_event.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class AddEventActivity : MvpBaseActivity(), AddEventView {

    @Inject
    lateinit var presenter: AddEventPresenter

    override val layout: Int
        get() = R.layout.activity_add_event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        presenter.attachView(this)
        setSupportActionBar(toolbar)



        test_btn.setOnClickListener {
            description_et.setText("Sri Sri Ravi Shankar travels to over 100 cities per year, spreading joy and wisdom. From slums to parliaments, he spreads a message of peace through yoga and meditation, and living with human values. and living with human values.")
            location_et.setText("San Francisco, California, United States")
            title_et.setText("Guru Purnima Celebrations")
        }
    }

    override fun showProgress() {
        progress_bar.show()
    }

    override fun hideProgress() {
        progress_bar.invisible()
    }

    override fun showError(message: String) {
        showToastEvent(message)
    }

    override fun exitActivity() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                findViewById<View>(android.R.id.content).hideKeyboard()
                presenter.saveEvent(title_et.getTrimmedText(), location_et.getTrimmedText(),
                        description_et.getTrimmedText(), start_date_et.getDate(),
                        start_time_et.getTime(), end_date_et.getDate(), end_time_et.getTime())
            }
            android.R.id.home -> {
                findViewById<View>(android.R.id.content).hideKeyboard()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}