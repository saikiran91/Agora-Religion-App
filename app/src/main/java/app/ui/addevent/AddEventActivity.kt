package app.ui.addevent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        initPickers()
    }

    private fun initPickers() {
        TextViewDatePicker(this, date_et)
        TextViewTimePicker(this, start_et)
        TextViewTimePicker(this, end_et)
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

                if ()

                    presenter.saveEvent(title_et.getTrimmedText(), location_et.getTrimmedText(),
                            description_et.getTrimmedText(), date_et.getTrimmedText(),
                            start_et.getTrimmedText(), end_et.getTrimmedText())
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}