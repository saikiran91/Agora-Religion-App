package app.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import timber.log.Timber
import java.util.*

class SetTime(val onCLickView: View, val is24Hours: Boolean, val listener: SetTime.SetonDateTimeSelectListener,
              val maxDate: Long? = null, val minDate: Long? = null, val addHour: Int? = null, val addDate: Int? = null,
              val showTime: Boolean = true) : View.OnClickListener, TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private val calendar: Calendar


    init {
        Timber.d("init  maxDate = %s, minDate = %S", maxDate?.run { Date(maxDate) }, minDate?.run { Date(minDate) })
        onCLickView.setOnClickListener(this)
        calendar = Calendar.getInstance()
    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Timber.d("onTimeSet  maxDate = %s, minDate = %S", maxDate?.run { Date(maxDate) }, minDate?.run { Date(minDate) })
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        listener.onDateTimeSelected(calendar, onCLickView)
    }

    override fun onClick(v: View) {
        Timber.d("onClick  maxDate = %s, minDate = %S", maxDate?.run { Date(maxDate) }, minDate?.run { Date(minDate) })
        v.hideKeyboard()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateDialog = DatePickerDialog(v.context, this, year, month, day)
        maxDate?.let { dateDialog.datePicker.maxDate = maxDate }
        minDate?.let { dateDialog.datePicker.minDate = minDate }
        addHour?.let { calendar.add(Calendar.HOUR, addHour) }
        dateDialog.setOnCancelListener { listener.onDateDismissed(calendar, onCLickView) }
        dateDialog.setOnDismissListener { listener.onDateDismissed(calendar, onCLickView) }
        dateDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        Timber.i("onDateSet ")
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        if (showTime) {

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timeDialog = TimePickerDialog(view.context, this, hour, minute, is24Hours)
            timeDialog.setOnCancelListener { listener.onTimeDismissed(calendar, onCLickView) }
            timeDialog.setOnDismissListener { listener.onTimeDismissed(calendar, onCLickView) }
            timeDialog.show()
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            listener.onDateTimeSelected(calendar, onCLickView)
        }
        Timber.d("selected time = %s", calendar.time)
    }

    interface SetonDateTimeSelectListener {
        fun onDateTimeSelected(calendar: Calendar, view: View)
        fun onDateDismissed(calendar: Calendar, view: View) {
        }

        fun onTimeDismissed(calendar: Calendar, view: View) {
        }
    }
}