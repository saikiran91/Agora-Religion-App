package app.data

import app.model.Event
import app.model.EventStatus
import io.reactivex.Completable
import io.reactivex.Observable


interface DataManager {
    fun saveEvent(event: Event): Completable
    fun fetchAndListenEvents(status: List<EventStatus>, forBroadcaster:Boolean =false): Observable<List<Event>>
}