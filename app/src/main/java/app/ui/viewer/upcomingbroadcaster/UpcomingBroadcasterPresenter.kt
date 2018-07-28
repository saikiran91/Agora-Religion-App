package app.ui.viewer.upcomingbroadcaster

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
import io.agora.religionapp.databinding.ItemUpcomingBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_upcoming.view.*
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class UpcomingBroadcasterPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<UpcomingBroadcasterView>() {
    private val listOfEvents = ObservableArrayList<Event>()

    private val lastAdapter: LastAdapter by lazy {
        LastAdapter(listOfEvents, BR.event).map<Event, ItemUpcomingBinding>(R.layout.item_upcoming){
            onBind { it.itemView.date_time_tv.text = it.binding.event?.parsedDate() }
        }
    }

    override fun attachView(mvpView: UpcomingBroadcasterView) {
        super.attachView(mvpView)
        mvpView.showProgress()
        addDisposable(dataManager
                .fetchAndListenEvents(listOf(EventStatus.NONE))
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