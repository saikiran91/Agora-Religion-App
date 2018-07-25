package app.ui.ongoingbroadcaster

import android.databinding.ObservableArrayList
import app.data.DataManager
import app.model.Event
import app.mvpbase.BasePresenter
import com.android.databinding.library.baseAdapters.BR
import com.github.nitrico.lastadapter.LastAdapter
import io.agora.religionapp.R
import io.agora.religionapp.databinding.ItemUpcomingBinding
import javax.inject.Inject

/**
 * Created by saiki on 22-07-2018.
 **/
class OngoingBroadcasterPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<OngoingBroadcasterView>() {
    private val listOfEvents = ObservableArrayList<Event>()

    private val lastAdapter: LastAdapter by lazy {
        LastAdapter(listOfEvents, BR.event).map<Event, ItemUpcomingBinding>(R.layout.item_upcoming)
    }

}