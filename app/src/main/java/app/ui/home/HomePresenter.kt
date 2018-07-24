package app.ui.home

import app.data.DataManager
import app.mvpbase.BasePresenter
import app.ui.ongoingbroadcaster.OngoingBroadcasterView
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class HomePresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<OngoingBroadcasterView>() {

}