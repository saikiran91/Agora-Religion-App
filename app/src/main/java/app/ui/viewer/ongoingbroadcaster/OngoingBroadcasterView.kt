package app.ui.viewer.ongoingbroadcaster

import app.mvpbase.MvpView
import com.github.nitrico.lastadapter.LastAdapter

/**
 * Created by saiki on 22-07-2018.
 **/
interface OngoingBroadcasterView : MvpView {
    fun showProgress()
    fun hideProgress()
    fun showError(message: String)
    fun setAdapter(lastAdapter: LastAdapter)
    fun launchLiveActivity(roomId: String)
}