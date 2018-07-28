package app.ui.addevent

import app.data.DataManager
import app.model.Event
import app.model.EventStatus
import app.mvpbase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class AddEventPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<AddEventView>() {
    fun saveEvent(title: String, location: String, description: String,
                  startDate: Calendar?, startTime: Calendar?, endDate: Calendar?, endTime: Calendar?) {
        when {
            startDate == null -> mvpView?.showError("Start Date can't be empty")
            startTime == null -> mvpView?.showError("Start time can't be empty")
            endDate == null -> mvpView?.showError("End Date can't be empty")
            endTime == null -> mvpView?.showError("End time can't be empty")
            title.isBlank() -> mvpView?.showError("Title can't be empty")
            location.isBlank() -> mvpView?.showError("Location can't be empty")
            description.isBlank() -> mvpView?.showError("Description can't be empty")


            else -> {

                val statDateAndTime = DateTime(startDate).withTime(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), 0, 0)
                val endDateAndTime = DateTime(endDate).withTime(endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), 0, 0)

                mvpView?.showProgress()
                addDisposable(dataManager.saveEvent(Event(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        location = location,
                        description = description,
                        start = statDateAndTime.millis,
                        end = endDateAndTime.millis,
                        lastUpdated = DateTime.now().millis,
                        eventStatus = EventStatus.NONE,
                        createdOn = DateTime.now().millis))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    mvpView?.hideProgress()
                                    mvpView?.exitActivity()
                                },
                                { t ->
                                    t.printStackTrace()
                                    mvpView?.hideProgress()
                                    mvpView?.showError(t.message ?: "saveEvent error")
                                }))
            }
        }

    }


}