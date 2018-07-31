package app.model

import app.util.parseDate
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import com.google.firebase.firestore.DocumentReference
import org.joda.time.DateTime

//Prefs
object UserPrefs : KotprefModel() {
    var userId by stringPref()
    var name by stringPref()
    var phone by stringPref()
    var countryCode by intPref()
    var user by enumValuePref(USER.NONE)
}

//Enums
enum class USER {
    NONE,
    VIEWER,
    BROADCASTER
}

enum class EventStatus {
    NONE,
    LIVE,
    COMPLETED
}

data class Question(val id: String = "",
                    val answered: Boolean = false,
                    val eventId: String = "",
                    val question: String = "",
                    val userId: String = "",
                    val userName: String = "",
                    var isRead: Boolean = false)

data class Chat(val id: String,
                val message: String,
                val userId: String,
                val userName: String,
                val eventId: String,
                val sendDate: Long)


//Models

data class FireUser(val userId: String = "", val name: String = "",
                    val phone: String = "0", val countryCode: Int = 0,
                    val lastUpdated: Long = 0L, val verified: Boolean = false) {
    fun getPhoneWithCountryCode() = "+$countryCode-$phone"
}

data class Event(val id: String = "", val title: String = "", val location: String = "",
                 val description: String = "", val start: Long = 0L,
                 val end: Long = 0L, val lastUpdated: Long = 0L,
                 val createdOn: Long = 0L, var user: DocumentReference? = null,
                 val eventStatus: EventStatus = EventStatus.NONE)


fun Event.parsedDate() = "${DateTime(start).parseDate()} - ${DateTime(end).parseDate()}".trim()




