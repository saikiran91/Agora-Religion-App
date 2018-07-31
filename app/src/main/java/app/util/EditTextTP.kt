package app.util

import android.app.TimePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.*

class EditTextTP : TextInputEditText, View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var calendar: Calendar

    init {
        setOnClickListener(this)
        isFocusable = false
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)

            val formatter24 = SimpleDateFormat(TIME_24_SERVER_PATTERN, Locale.ENGLISH)
            val formatter12 = SimpleDateFormat(TIME_12_SERVER_PATTERN, Locale.ENGLISH)
            setText(if (DateFormat.is24HourFormat(context))
                formatter24.format(time)
            else
                formatter12.format(time))
        }
    }

    override fun onClick(v: View) {
        Calendar.getInstance(TimeZone.getDefault()).run {
            TimePickerDialog(context, this@EditTextTP,
                    get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(context)).show()
        }
    }

    fun getTime() = when {
        ::calendar.isInitialized -> calendar
        else -> null
    }

    companion object {
        const val TIME_24_SERVER_PATTERN = "HH:mm"
        const val TIME_12_SERVER_PATTERN = "hh:mm aa"
    }
}