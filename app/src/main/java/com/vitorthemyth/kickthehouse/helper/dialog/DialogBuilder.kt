package com.vitorthemyth.kickthehouse.helper.dialog

import android.app.AlertDialog
import android.content.Context
import com.vitorthemyth.kickthehouse.R

fun showDialog(context: Context, title: String, message: String, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(context.getString(R.string.ok_btn)) { dialog, _ ->
        onPositiveClick()
        dialog.dismiss()
    }
    builder.setNegativeButton(null) { dialog, _ ->
        onNegativeClick()
        dialog.dismiss()
    }
    val dialog = builder.create()
    dialog.show()
}