package com.vitorthemyth.kickthehouse.helper.date_picker

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

fun showDatePickerDialog(context: Context, initialDate: Calendar?, onDateSelected: (String) -> Unit) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(selectedDate.time)
        onDateSelected(dateString)
    }

    val datePickerDialog = DatePickerDialog(context, listener,
        initialDate?.get(Calendar.YEAR) ?: Calendar.getInstance().get(Calendar.YEAR),
        initialDate?.get(Calendar.MONTH) ?: Calendar.getInstance().get(Calendar.MONTH),
        initialDate?.get(Calendar.DAY_OF_MONTH) ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    datePickerDialog.datePicker.minDate = 0


    datePickerDialog.show()
    datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).text = context.getString(android.R.string.ok)
    datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).text = context.getString(android.R.string.cancel)
    datePickerDialog.setOnCancelListener {
        val dateFormatOnCancel = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStringOnCancel = dateFormatOnCancel.format(initialDate?.time ?: Calendar.getInstance().time)
        onDateSelected(dateStringOnCancel)
    }

    // Customize the date picker dialog
    datePickerDialog.setOnShowListener { _ ->
        val dateTextView = datePickerDialog.findViewById<TextView>(context.resources.getIdentifier("android:id/date_picker_header_date", null, null))
        dateTextView.text = initialDate?.let { dateFormat.format(it.time) } ?: dateFormat.format(Calendar.getInstance().time)
        dateTextView.setTextColor(ContextCompat.getColor(context, com.vitorthemyth.components.R.color.orange))
    }
}
