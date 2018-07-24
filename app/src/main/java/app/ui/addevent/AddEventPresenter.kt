package app.ui.addevent

import app.data.DataManager
import app.model.Event
import app.mvpbase.BasePresenter
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class AddEventPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<AddEventView>() {
    fun saveEvent(title: String, location: String, description: String,
                  date: DateTime, start: DateTime, end: DateTime) {

        when {
            title.isBlank() -> mvpView?.showError("Title can't be empty")
            location.isBlank() -> mvpView?.showError("Location can't be empty")
            description.isBlank() -> mvpView?.showError("Description can't be empty")
            date.isBlank() -> mvpView?.showError("Date can't be empty")
            start.isBlank() -> mvpView?.showError("Start can't be empty")
            end.isBlank() -> mvpView?.showError("End can't be empty")
            else->dataManager.saveEvent(Event(title = title,location = location,
                    description = description,date = Date().time,start = start,end = end))
        }

    }

    fun validateFields()

}