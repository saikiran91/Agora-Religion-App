package app.util

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.*

class EditTextDP : TextInputEditText, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var calendar: Calendar

    init {
        setOnClickListener(this)
        isFocusable = false
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthOfYear)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val formatter = SimpleDateFormat(DATE_SERVER_PATTERN,
                    Locale.ENGLISH)
            setText(formatter.format(this.time))
        }
    }

    override fun onClick(v: View) {
        Calendar.getInstance(TimeZone.getDefault()).run {
            DatePickerDialog(context, this@EditTextDP,
                    get(Calendar.YEAR), get(Calendar.MONTH),
                    get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    fun getDate() = when {
        ::calendar.isInitialized -> calendar
        else -> throw (RuntimeException("Date not set"))
    }

    companion object {
        const val DATE_SERVER_PATTERN = "dd-MM-yyyy"
    }
}