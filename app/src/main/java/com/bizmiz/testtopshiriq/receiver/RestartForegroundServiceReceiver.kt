package com.bizmiz.testtopshiriq.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bizmiz.testtopshiriq.service.MyForegroundService
import javax.inject.Inject


class RestartForegroundServiceReceiver @Inject constructor(
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(Intent(context, MyForegroundService::class.java))
        } else {
            context!!.startService(Intent(context, MyForegroundService::class.java))
        }
    }
}