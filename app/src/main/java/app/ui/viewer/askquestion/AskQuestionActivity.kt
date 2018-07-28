package app.ui.viewer.askquestion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.model.Question
import app.model.UserPrefs
import app.ui.liveboradcaster.VideoChatViewActivity.Companion.CHAT_ROOM_KEY
import app.util.getTrimmedText
import app.util.hide
import app.util.show
import app.util.showToastEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.agora.religionapp.BuildConfig
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_ask_question.*
import java.util.*

class AskQuestionActivity : AppCompatActivity() {

    private val fireStoreDB = FirebaseFirestore.getInstance().apply {
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .setPersistenceEnabled(true)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_question)
        ask_btn.setOnClickListener {
            if (question_et.getTrimmedText().isNotBlank()) {
                val question = Question(id = UUID.randomUUID().toString(),
                        answered = false, eventId = intent.getStringExtra(CHAT_ROOM_KEY), question = question_et.getTrimmedText(),
                        userId = UserPrefs.userId, userName = UserPrefs.name, isRead = false)
                progress_bar.show()
                fireStoreDB.collection("question")
                        .document(question.id)
                        .set(question)
                        .addOnSuccessListener {

                            progress_bar.hide()

                            showToastEvent("Question sent")
                            finish()
                        }
                        .addOnFailureListener {
                            progress_bar.hide()
                            showToastEvent("Error while posting question, please try again")
                        }
            } else {
                showToastEvent("Question cannot be empty")
            }
        }

    }
}
