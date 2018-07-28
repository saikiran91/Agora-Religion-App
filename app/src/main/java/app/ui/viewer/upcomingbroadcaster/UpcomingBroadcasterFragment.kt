package app.ui.viewer.upcomingbroadcaster

import android.os.Bundle
import app.mvpbase.MvpBaseFragment
import app.util.hide
import app.util.show
import app.util.showToastEvent
import com.github.nitrico.lastadapter.LastAdapter
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.fragment_broadcaster_upcoming.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class UpcomingBroadcasterFragment : MvpBaseFragment(), UpcomingBroadcasterView {

    @Inject
    lateinit var presenter: UpcomingBroadcasterPresenter

    override val layout: Int
        get() = R.layout.fragment_broadcaster_upcoming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.attachView(this)
    }

    override fun setAdapter(lastAdapter: LastAdapter) {
        event_list.adapter = lastAdapter
    }

    override fun showProgress() {
        progress_bar.show()
    }

    override fun hideProgress() {
        progress_bar.hide()
    }

    override fun showError(message: String) {
        showToastEvent(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}