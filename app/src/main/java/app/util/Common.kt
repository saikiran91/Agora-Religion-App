package app.util


import android.app.AlertDialog
import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import app.model.ShowToastEvent
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*


fun Date.getSimpleTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun View.visible(b: Boolean = true) {
    if (b) show() else hide()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun View.hideKeyboard() {
    val input = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    input.hideSoftInputFromWindow(this.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}


fun View.showKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun <E> MutableCollection<E>.clearAndAddAll(replace: Collection<E>) {
    clear()
    addAll(replace)
}

fun Context.showAlertDialog(message: String, title: String = "Alert") {
    val dialog = AlertDialog.Builder(this)
    dialog.setMessage(message)
    dialog.setTitle(title)
    dialog.setPositiveButton("OK") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }
    dialog.show()
}

fun Context.showDialogWithAction(message: String, title: String = "Alert", onPositiveClick: (() -> Unit)? = null) {
    val dialog = AlertDialog.Builder(this)
    dialog.setMessage(message)
    dialog.setTitle(title)
    dialog.setPositiveButton("OK") { dialogInterface, _ ->
        onPositiveClick?.invoke()
        dialogInterface.dismiss()
    }
    dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }
    dialog.show()
}

fun TextInputLayout.setUpTextChangeListenerToClearError(view: EditText) {
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            this@setUpTextChangeListenerToClearError.error = ""
        }
    })
}

fun EventBus.regOnce(subscriber: Any) {
    if (!isRegistered(subscriber)) register(subscriber)
}

fun EventBus.unregOnce(subscriber: Any) {
    if (isRegistered(subscriber)) unregister(subscriber)
}

fun showToastEvent(message: Any?) {
    EventBus.getDefault().post(ShowToastEvent(message = message.toString()))
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction { replace(frameId, fragment) }
}

fun EditText.getTrimmedText() = text.toString().trim()

