package app.model

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref

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

//Models

data class FireUser(val userId: String = "", val name: String = "",
                    val phone: String = "0", val countryCode: Int = 0,
                    val lastUpdated: Long = 0L, val verified: Boolean = false) {
    fun getPhoneWithCountryCode() = "+$countryCode-$phone"
}

data class Event(val id: String = "", val title: String = "", val location: String = "",
                 val description: String = "", val date: Long = 0L, val start: Long = 0L,
                 val end: Long = 0L, val lastUpdated: Long,
                 val createdOn: Long = 0L, val user: FireUser = FireUser())






