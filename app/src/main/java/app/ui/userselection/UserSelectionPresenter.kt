package app.ui.userselection

import app.data.DataManager
import app.model.USER
import app.mvpbase.BasePresenter
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class UserSelectionPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<UserSelectionView>() {

    fun onBroadcasterSelected() {
        mvpView?.launchBroadcasterRegistration(USER.BROADCASTER)
        mvpView?.exitActivity()
    }

    fun onViewerSelected() {
        mvpView?.launchViewerRegistration(USER.VIEWER)
        mvpView?.exitActivity()
    }
}