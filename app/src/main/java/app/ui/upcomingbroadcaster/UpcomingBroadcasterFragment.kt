package app.ui.upcomingbroadcaster

import android.databinding.ObservableArrayList
import android.os.Bundle
import app.mvpbase.MvpBaseFragment
import app.ui.ongoingbroadcaster.OngoingBroadcasterPresenter
import app.ui.ongoingbroadcaster.OngoingBroadcasterView
import io.agora.religionapp.R
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class UpcomingBroadcasterFragment : MvpBaseFragment(), OngoingBroadcasterView {

    @Inject
    lateinit var presenter: OngoingBroadcasterPresenter


    override val layout: Int
        get() = R.layout.fragment_broadcaster_upcoming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        presenter.attachView(this)
    }

    override fun showProgress() {}

    override fun hideProgress() {}

    override fun showError(message: String) {}

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}