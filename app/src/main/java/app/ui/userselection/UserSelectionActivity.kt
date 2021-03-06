package app.ui.userselection

import android.content.Intent
import android.os.Bundle
import android.view.View
import app.model.USER
import app.mvpbase.MvpBaseActivity
import app.ui.registration.RegistrationActivity
import app.util.showToastEvent
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_user_selection.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class UserSelectionActivity : MvpBaseActivity(), UserSelectionView {
    override fun exitActivity() {
        finish()
    }

    @Inject
    lateinit var presenter: UserSelectionPresenter

    override val layout: Int
        get() = R.layout.activity_user_selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        presenter.attachView(this)

        //By default it is Broadcaster
        presenter.onBroadcasterSelected()

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.viewer_radio -> presenter.onViewerSelected()
                R.id.broadcaster_radio -> presenter.onBroadcasterSelected()
                else -> showToastEvent("$checkedId not found")
            }
        }
    }

    override fun launchBroadcasterRegistration(user: USER) {
        startActivity(Intent(this, RegistrationActivity::class.java)
                .apply { putExtra("USER", user) })
    }

    override fun launchViewerRegistration(user: USER) {
        startActivity(Intent(this, RegistrationActivity::class.java)
                .apply { putExtra("USER", user) })
    }

    override fun showError(message: String) {
        showToastEvent(message)
    }

    fun onContinueClicked(view: View) {
        presenter.onContinueClicked()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}