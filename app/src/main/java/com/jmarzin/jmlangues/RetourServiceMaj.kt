package com.jmarzin.jmlangues

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast

/**
 * Created by jacques on 07/01/15.
 */
class RetourServiceMaj : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        val message = extras!!.getString("message")
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, message, duration)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }
}