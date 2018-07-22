package app.ui.registration

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import app.model.USER
import app.model.UserPrefs
import app.util.hide
import app.util.hideKeyboard
import app.util.show
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.agora.religionapp.BuildConfig
import io.agora.religionapp.R
import kotlinx.android.synthetic.main.activity_auth_content.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


class RegistrationActivity : AppCompatActivity() {

    private var verificationId: String? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val fireStoreDB = FirebaseFirestore.getInstance().apply {
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)
        firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Timber.d("verification completed %s", credential)
            showProcessBar(false)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Timber.w(e, "verification failed")
            if (e is FirebaseAuthInvalidCredentialsException) {
                number_et.error = "Invalid phone number."
                Toast.makeText(this@RegistrationActivity,
                        "The format of the phone number provided is incorrect. "
                                + "The phone number must be in the format [+][country code][subscriber number].",
                        Toast.LENGTH_LONG).show()

            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(this@RegistrationActivity,
                        "Too many attempts. Please try after some time",
                        Toast.LENGTH_SHORT).show()
            }
            showProcessBar(false)
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            Timber.d("code sent %s", verificationId)
            this@RegistrationActivity.verificationId = verificationId
            showVerificationGroup()
            updateChangePhoneNumberText(phoneNumberWithCountryCode)
            showProcessBar(false)
        }
    }

    private val phoneNumber: String
        get() = number_et.text.toString().trim { it <= ' ' }

    private val phoneNumberWithCountryCode: String
        get() = country_code_picker.selectedCountryCodeWithPlus + number_et.text.toString().trim { it <= ' ' }

    private val preferredName: String
        get() = name_et.text.toString().trim { it <= ' ' }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (firebaseAuth.currentUser != null && UserPrefs.userId.isNotBlank())
            launchNextActivity()
    }

    fun sendCodeOnClick(view: View) {
        view.hideKeyboard()
        sendSmsCode()
    }

    fun verifyCodeOnClick(view: View) {
        view.hideKeyboard()
        verifySmsCode()
    }

    fun signOuOnClick(view: View) {
        view.hideKeyboard()
        signOut()
    }

    fun changePhoneNumber(view: View) {
        showProcessBar(false)
        auth_et.text.clear()
        view.hideKeyboard()
        signOut()
    }

    private fun updateChangePhoneNumberText(phoneNumber: String) {
        val tv = verification_tv
        if (tv != null) {
            val text = "Verification code sent to $phoneNumber"
            tv.text = text
        }
    }

    private fun sendSmsCode() {
        if (!validatePhoneNumberAndName(phoneNumberWithCountryCode)) {
            return
        }
        verifyPhoneNumber(phoneNumberWithCountryCode)
    }

    private fun validatePhoneNumberAndName(phoneNumber: String): Boolean {
        if (TextUtils.isEmpty(phoneNumber)) {
            number_et.error = "Can't be empty"
            return false
        } else if (TextUtils.isEmpty(preferredName)) {
            name_et.error = "Can't be empty"
            return false
        }
        return true
    }

    private fun verifyPhoneNumber(phno: String) {
        showProcessBar(true)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phno, 70,
                TimeUnit.SECONDS, this, callbacks)
    }

    private fun verifySmsCode() {
        if (verificationId != null) {
            val verificationCode = auth_et.text.toString().trim { it <= ' ' }
            val credential = PhoneAuthProvider.getCredential(verificationId!!, verificationCode)
            signInWithPhoneAuthCredential(credential)
        } else {
            Timber.e("verificationId is null")
            Toast.makeText(this@RegistrationActivity,
                    "Something went wrong please try again.",
                    Toast.LENGTH_SHORT).show()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showProcessBar(true)
        auth_et.setText(credential.smsCode)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    showProcessBar(false)
                    if (task.isSuccessful) {
                        Timber.d("code verified signIn successful")
                        addUserToFireStore(task.result.user)
                    } else {
                        Timber.w(task.exception, "code verification failed")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            auth_et.error = "Invalid code."
                        }
                    }
                }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        UserPrefs.clear()
        showLoginGroup()
    }

    private fun addUserToFireStore(user: FirebaseUser) {
        showProcessBar(true)
        val userDetails = mapOf("userId" to user.uid, "name" to preferredName, "phone" to phoneNumber,
                "countryCode" to country_code_picker.defaultCountryCodeAsInt,
                "lastUpdated" to Timestamp.now(), "verified" to true, "user" to intent.getSerializableExtra("USER"))

        fireStoreDB.collection("users").document(user.uid)
                .set(userDetails)
                .addOnFailureListener(this) { Timber.e(it, "Error adding phone auth info") }
                .addOnFailureListener { Timber.e(it, "Error adding phone auth info") }
                .addOnSuccessListener {
                    Timber.d("DocumentSnapshot successfully written!")
                    showProcessBar(false)
                    saveUserDetailsToPrefs(user.uid, preferredName, phoneNumber, country_code_picker.defaultCountryCodeAsInt)
                    launchNextActivity()
                }
                .addOnFailureListener { e ->
                    Timber.e(e, "Error adding phone auth info")
                    showProcessBar(false)
                }
    }

    private fun launchNextActivity() {
        // showSignoutGroup();
//        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun saveUserDetailsToPrefs(uid: String, preferredName: String, phoneNumber: String, defaultCountryCodeAsInt: Int) {
        UserPrefs.userId = uid
        UserPrefs.name = preferredName
        UserPrefs.phone = phoneNumber
        UserPrefs.countryCode = defaultCountryCodeAsInt
        UserPrefs.user = intent.getSerializableExtra("USER") as USER
    }

    private fun showLoginGroup() {
        verification_code_group.hide()
        send_code_group.show()
        logout_group.hide()
    }

    private fun showVerificationGroup() {
        verification_code_group.show()
        send_code_group.hide()
        logout_group.hide()
    }

    private fun showSignoutGroup() {
        send_code_group.hide()
        verification_code_group.hide()
        logout_group.show()
    }

    private fun showProcessBar(visibility: Boolean) {
        progress_bar.visibility = if (visibility) View.VISIBLE else View.GONE
    }
}