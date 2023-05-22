package com.vitorthemyth.kickthehouse.helper.dialog

import android.app.AlertDialog
import android.content.Context
import com.vitorthemyth.kickthehouse.R

/**
 * Listener used to setup callbacks on dialogs.
 * Who is showing the dialog, must implement it and handle the callback, parsing the dialog tag,
 * the request code and/or the result data
 */
abstract class DialogInteractionListener {

    /**
     * Callback for success cases
     */
    open fun onPositiveCallback(tag: String? = null, data: Any? = null) {}

    /**
     * Callback for failure or negative cases
     */
    open fun onNegativeCallback(tag: String? = null, data: Any? = null) {}
}


