package app.ui.userselection

import app.model.USER
import app.mvpbase.MvpView

/**
 * Created by saiki on 22-07-2018.
 **/
interface UserSelectionView : MvpView {
    fun launchBroadcasterRegistration(user: USER)
    fun launchViewerRegistration(user: USER)
    fun showError(message: String)
}