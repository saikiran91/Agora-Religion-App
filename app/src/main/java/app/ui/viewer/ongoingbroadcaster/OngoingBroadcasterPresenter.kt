package app.ui.viewer.ongoingbroadcaster

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
import io.agora.religionapp.databinding.ItemViewerOngoingBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_viewer_ongoing.view.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class OngoingBroadcasterPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<OngoingBroadcasterView>() {
    private val listOfEvents = ObservableArrayList<Event>()

    private val lastAdapter: LastAdapter by lazy {
        LastAdapter(listOfEvents, BR.event).map<Event, ItemViewerOngoingBinding>(R.layout.item_viewer_ongoing) {
            onBind { item ->
                item.itemView.date_time_tv.text = item.binding.event?.parsedDate()
                item.binding.event?.let {
                    item.itemView.start_btn.setOnClickListener { launchLiveActivity(item.binding.event!!) }
                }
            }
        }
    }

    private fun launchLiveActivity(event: Event) {
        mvpView?.launchLiveActivity(roomId = event.id)
    }

    override fun attachView(mvpView: OngoingBroadcasterView) {
        super.attachView(mvpView)
        mvpView.showProgress()
        addDisposable(dataManager
                .fetchAndListenEvents(listOf(EventStatus.LIVE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listOfEvents.clearAndAddAll(it)
                    mvpView.hideProgress()
                }, {
                    it.printStackTrace()
                    mvpView.showError(it.message ?: "fetchAndListenEvents failed")
                    mvpView.hideProgress()
                }))

        mvpView.setAdapter(lastAdapter)
    }
}