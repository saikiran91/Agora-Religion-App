package app.util

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TextViewDatePicker @JvmOverloads
constructor(private val mContext: Context, private val mView: TextView,
            private var mMinDate: Long = 0, private var mMaxDate: Long = 0) :
        View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private lateinit var mDatePickerDialog: DatePickerDialog
    private lateinit var calendar: Calendar

    init {
        mView.setOnClickListener(this)
        mView.isFocusable = false
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date = calendar.time

        val formatter = SimpleDateFormat(DATE_SERVER_PATTERN, Locale.ENGLISH)
        mView.text = formatter.format(date)
    }

    override fun onClick(v: View) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        mDatePickerDialog = DatePickerDialog(mContext, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        if (mMinDate != 0L) {
            mDatePickerDialog.datePicker.minDate = mMinDate
        }
        if (mMaxDate != 0L) {
            mDatePickerDialog.datePicker.maxDate = mMaxDate
        }
        mDatePickerDialog.show()
    }

    fun setMinDate(minDate: Long) {
        mMinDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        mMaxDate = maxDate
    }

    fun getDate() = when {
        ::calendar.isInitialized -> calendar
        else -> throw (RuntimeException("Date not set"))
    }

    companion object {
        const val DATE_SERVER_PATTERN = "yyyy-MM-dd"
    }
}