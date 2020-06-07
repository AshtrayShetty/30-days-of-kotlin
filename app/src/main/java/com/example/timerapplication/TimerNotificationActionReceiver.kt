package com.example.timerapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timerapplication.util.NotificationUtil
import com.example.timerapplication.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when(intent.action){
            AppConstants.ACTION_STOP->{
                MainActivity.removeAlarm(context)
                PrefUtil.setTimerState(MainActivity.TimerState.STOPPED, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE->{
                var secondsRemaining=PrefUtil.getTimerSecondsRemaining(context)
                val alarmSetTime=PrefUtil.getAlarmSetTime(context)
                val nowSeconds=MainActivity.nowSeconds

                secondsRemaining-=nowSeconds-alarmSetTime
                PrefUtil.setTimerSecondsRemaining(secondsRemaining, context)
                MainActivity.removeAlarm(context)

                PrefUtil.setTimerState(MainActivity.TimerState.PAUSED, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME->{
                var secondsRemaining=PrefUtil.getTimerSecondsRemaining(context)
                val wakeUpTime=MainActivity.setAlarm(context, MainActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(MainActivity.TimerState.RUNNING, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            AppConstants.ACTION_START->{
                val minutesRemaining=PrefUtil.getTimerLength(context)
                val secondsRemaining=minutesRemaining*60L
                val wakeUpTime=MainActivity.setAlarm(context, MainActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(MainActivity.TimerState.RUNNING, context)
                PrefUtil.setTimerSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
