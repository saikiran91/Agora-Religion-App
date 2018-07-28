package app.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.View
import app.mvpbase.MvpBaseActivity
import app.ui.addevent.AddEventActivity
import app.ui.ongoingbroadcaster.OngoingBroadcasterFragment
import app.ui.upcomingbroadcaster.UpcomingBroadcasterFragment
import app.util.replaceFragment
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_home.*
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

        showFragment(OngoingBroadcasterFragment())

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> showFragment(OngoingBroadcasterFragment())
                    1 -> showFragment(UpcomingBroadcasterFragment())
                }
            }
        })
    }

    private fun showFragment(fragment: Fragment) = replaceFragment(fragment, R.id.fragment_container)


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