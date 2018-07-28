package app.data

import app.model.Event
import app.model.EventStatus
import app.model.UserPrefs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.agora.religionapp.BuildConfig
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
internal class DataManagerImpl @Inject constructor() : DataManager {

    private val fireStoreDB = FirebaseFirestore.getInstance().apply {
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build()
    }

    override fun saveEvent(event: Event): Completable {
        return Completable.create { emitter ->
            fireStoreDB.collection("event")
                    .document(event.id)
                    .set(event.apply {
                        user = fireStoreDB.collection("user")
                                .document(UserPrefs.userId)
                    })
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }.subscribeOn(Schedulers.io())
    }

    override fun fetchAndListenEvents(status: List<EventStatus>, forBroadcaster: Boolean): Observable<List<Event>> {
        return Observable.create<List<Event>> { emitter ->
            fireStoreDB.collection("event")
                    .addSnapshotListener { result, exception ->
                        if (exception != null) emitter.onError(exception)
                        if (result == null) emitter.onError(NullPointerException("fetchAndListenEvents result is null"))
                        try {
                            val list = result!!.toObjects(Event::class.java)

                            val validList = if (forBroadcaster) {
                                list.filter { it.user == fireStoreDB.collection("user").document(UserPrefs.userId) }
                                        .filter { status.firstOrNull { status -> status == it.eventStatus } != null }
                            } else {
                                list.filter { status.firstOrNull { status -> status == it.eventStatus } != null }
                            }
                            emitter.onNext(validList)
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
        }.subscribeOn(Schedulers.io())
    }


}