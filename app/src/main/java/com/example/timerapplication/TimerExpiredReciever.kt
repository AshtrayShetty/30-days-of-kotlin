package com.example.timerapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timerapplication.util.NotificationUtil
import com.example.timerapplication.util.PrefUtil

class TimerExpiredReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(MainActivity.TimerState.STOPPED, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
