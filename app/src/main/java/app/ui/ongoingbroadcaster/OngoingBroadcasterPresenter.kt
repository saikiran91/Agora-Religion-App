package app.ui.ongoingbroadcaster

import android.databinding.ObservableArrayList
import app.data.DataManager
import app.model.Event
import app.model.EventStatus
import app.model.parsedDate
import app.mvpbase.BasePresenter
import app.util.clearAndAddAll
import com.android.databinding.library.baseAdapters.BR
import com.github.nitrico.lastadapter.LastAdapter
import io.agora.religionapp.R
import io.agora.religionapp.databinding.ItemOngoingBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_ongoing.view.*
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class OngoingBroadcasterPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<OngoingBroadcasterView>() {
    private val listOfEvents = ObservableArrayList<Event>()

    private val lastAdapter: LastAdapter by lazy {
        LastAdapter(listOfEvents, BR.event).map<Event, ItemOngoingBinding>(R.layout.item_ongoing) {
            onBind { item ->
                Timber.d("onBind")
                item.itemView.date_time_tv.text = item.binding.event!!.parsedDate()
                item.itemView.start_btn.setOnClickListener { launchLiveBroadcasterActivity(item.binding.event!!) }
                item.itemView.end_btn.setOnClickListener { endBroadcast(item.binding.event!!) }
            }
            onClick {
                // if onClick is not there when we come back form some activity start_btn and end_btn does not work.
                Timber.d("onBind, %s", it.itemView)
            }
        }
    }

    private fun endBroadcast(event: Event) {
        addDisposable(dataManager.saveEvent(event.copy(eventStatus = EventStatus.COMPLETED)).subscribe())
        mvpView?.showError("Event completed")
    }

    private fun launchLiveBroadcasterActivity(event: Event) {
        addDisposable(dataManager.saveEvent(event.copy(eventStatus = EventStatus.LIVE)).subscribe())
        mvpView?.launchLiveBroadcasterActivity(roomId = event.id)
    }

    override fun attachView(mvpView: OngoingBroadcasterView) {
        super.attachView(mvpView)
        mvpView.showProgress()
        addDisposable(dataManager
                .fetchAndListenEvents(listOf(EventStatus.NONE, EventStatus.LIVE), forBroadcaster = true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val validSchedule = it.filter { (interval(Date(it.start), Date()) < 60) || it.eventStatus == EventStatus.LIVE }
                    listOfEvents.clearAndAddAll(validSchedule)
                    mvpView.hideProgress()
                }, {
                    it.printStackTrace()
                    mvpView.showError(it.message ?: "fetchAndListenEvents failed")
                    mvpView.hideProgress()
                }))

        mvpView.setAdapter(lastAdapter)
    }


    private fun interval(first: Date, second: Date): Int {
        val diff = DateTime(first).minuteOfDay().get() - DateTime(second).minuteOfDay().get()
        return Math.abs(diff)
    }
}