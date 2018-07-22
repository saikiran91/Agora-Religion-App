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

    private var user: USER = USER.NONE

    fun onBroadcasterSelected() {
        user = USER.BROADCASTER
    }

    fun onViewerSelected() {
        user = USER.VIEWER
    }

    fun onContinueClicked() {
        when (user) {
            USER.NONE -> mvpView?.showError("Please select the user type")
            USER.VIEWER -> mvpView?.launchViewerRegistration(user)
            USER.BROADCASTER -> mvpView?.launchBroadcasterRegistration(user)
        }
    }
}