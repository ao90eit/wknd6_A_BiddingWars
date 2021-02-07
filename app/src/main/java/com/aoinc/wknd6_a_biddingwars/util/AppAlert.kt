package com.aoinc.wknd6_a_biddingwars.util

import android.content.Context
import android.widget.Toast

object AppAlert {
    fun makeToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, msg, length).show()
    }
}