package com.aoinc.wknd6_a_biddingwars.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast

object AppAlert {
    fun makeToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, msg, length).show()
    }

    fun makeSimpleDialog(context: Context, message: String, btnText: String,
                         listener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(btnText, listener)
            .create()
            .show()
    }
}