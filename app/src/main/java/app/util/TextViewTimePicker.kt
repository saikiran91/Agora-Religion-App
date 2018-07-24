package app.util

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.*

class TextViewTimePicker(private val context: Context,
                         private val view: TextView) :
        View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var calendar: Calendar

    init {
        view.setOnClickListener(this)
        view.isFocusable = false
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)

            val formatter24 = SimpleDateFormat(TIME_24_SERVER_PATTERN, Locale.ENGLISH)
            val formatter12 = SimpleDateFormat(TIME_12_SERVER_PATTERN, Locale.ENGLISH)
            this@TextViewTimePicker.view.text = if (DateFormat.is24HourFormat(context))
                formatter24.format(calendar.time)
            else
                formatter12.format(calendar.time)
        }
    }

    override fun onClick(v: View) {
        Calendar.getInstance(TimeZone.getDefault()).run {
            TimePickerDialog(context, this@TextViewTimePicker,
                    get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE),
                    !DateFormat.is24HourFormat(context)).show()
        }
    }

    fun getTime() = when {
        ::calendar.isInitialized -> calendar
        else -> throw (RuntimeException("Time not set"))
    }

    companion object {
        const val TIME_24_SERVER_PATTERN = "HH:mm"
        const val TIME_12_SERVER_PATTERN = "hh:mm aa"
    }
}