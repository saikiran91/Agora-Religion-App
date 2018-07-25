package app.data

import app.model.Event
import io.reactivex.Completable


interface DataManager {
    fun saveEvent(event: Event): Completable
}