package com.vitorthemyth.kickthehouse.helper.dialog

import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.vitorthemyth.kickthehouse.R

fun DialogFragment.setFullScreen() {
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog)

    dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    dialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}