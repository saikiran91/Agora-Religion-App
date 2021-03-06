package app.ui.liveboradcaster

import app.data.DataManager
import app.mvpbase.BasePresenter
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class LiveBroadcasterPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<LiveBroadcasterView>() {

}