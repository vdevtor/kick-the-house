package com.vitorthemyth.kickthehouse.helper.strings_numers

import android.text.Editable
import android.text.TextWatcher
import java.text.DecimalFormat

val String.Companion.Empty
    inline get() = ""

val String.Companion.DASH
    inline get() = "-"

val Int.Companion.Empty
    inline get() = 0

val Double.Companion.Empty
    inline get() = 0.0

val Boolean.Companion.Default
    inline  get() = false


// Define a TextWatcher for the money mask
val moneyTextWatcher: TextWatcher = object : TextWatcher {
    private val df: DecimalFormat = DecimalFormat("#,##0.00")
    private var editing: Boolean = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // No action needed
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // No action needed
    }

    override fun afterTextChanged(s: Editable?) {
        if (!editing) {
            editing = true

            val originalText = s.toString()

            // Remove previous formatting
            val cleanText = originalText.replace("[^\\d]".toRegex(), "")

            // Parse the cleaned text as a long value
            val parsedValue = cleanText.toLongOrNull() ?: 0L

            // Convert the parsed value to cents (divide by 100)
            val centsValue = parsedValue / 100.0

            // Format the cents value as currency
            val formattedText = df.format(centsValue)

            // Set the formatted text back to the EditText
            s?.replace(0, s.length, formattedText)

            editing = false
        }
    }
}


