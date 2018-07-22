package app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.agora.religionapp.BuildConfig
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
}