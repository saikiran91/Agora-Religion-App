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







