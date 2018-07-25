package app.data

import app.model.Event
import app.model.UserPrefs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.agora.religionapp.BuildConfig
import io.reactivex.Completable
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
}