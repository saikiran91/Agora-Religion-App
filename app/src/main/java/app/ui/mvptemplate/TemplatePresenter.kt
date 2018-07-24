package app.ui.mvptemplate

import app.data.DataManager
import app.mvpbase.BasePresenter
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class TemplatePresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<TemplateView>() {

}