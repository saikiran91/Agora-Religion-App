package app.ui.upcomingbroadcaster

import app.mvpbase.MvpView
import com.github.nitrico.lastadapter.LastAdapter

/**
 * Created by saiki on 22-07-2018.
 **/
interface UpcomingBroadcasterView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
    fun setAdapter(lastAdapter: LastAdapter)

}