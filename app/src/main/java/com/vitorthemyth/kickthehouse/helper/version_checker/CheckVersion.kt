package com.vitorthemyth.kickthehouse.helper.version_checker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.vitorthemyth.kickthehouse.R

const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.vitorthemyth.kickthehouse"

fun Context.checkInAppUpdateNeeded() {
    val appUpdateManager = AppUpdateManagerFactory.create(this)

    // Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    // Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        ) {
            AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.txt_attention))
                .setMessage(this.getString(R.string.txt_update_available_msg))
                .setNegativeButton(this.getString(R.string.txt_update_decline)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(this.getString(R.string.txt_update)) { dialog, _ ->
                    dialog.dismiss()
                    Intent(Intent.ACTION_VIEW).also {
                        it.data = Uri.parse(GOOGLE_PLAY_URL)
                        startActivity(it)
                    }
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }
}