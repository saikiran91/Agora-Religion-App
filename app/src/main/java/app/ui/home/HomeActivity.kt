package app.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.model.UserPrefs
import app.mvpbase.MvpBaseActivity
import app.ui.addevent.AddEventActivity
import app.ui.ongoingbroadcaster.OngoingBroadcasterFragment
import app.ui.upcomingbroadcaster.UpcomingBroadcasterFragment
import app.ui.userselection.UserSelectionActivity
import app.util.replaceFragment
import com.google.firebase.auth.FirebaseAuth
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

        bottom_navigation.setOnNavigationItemSelectedListener {

            appbar_layout.setExpanded(true, true)


            when (it.itemId) {
                R.id.menu_live -> showFragment(OngoingBroadcasterFragment())
                R.id.menu_upcoming -> showFragment(UpcomingBroadcasterFragment())
            }
            true
        }

        setSupportActionBar(toolbar)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_signout -> {
                FirebaseAuth.getInstance().signOut()
                UserPrefs.clear()
                finish()
                startActivity(Intent(this, UserSelectionActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}