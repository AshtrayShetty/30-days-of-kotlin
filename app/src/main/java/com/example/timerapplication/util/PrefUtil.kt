package com.example.timerapplication.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.timerapplication.MainActivity

class PrefUtil {
    companion object{

        private const val TIMER_LENGTH_ID="com.example.timerapplication.timer_length"

        fun getTimerLength(context: Context): Int{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 10)
        }

        // Preferences are used to store the values of some variables
        // even when app is killed
        // Similar to a dictionary or key-value pairs
        private const val PREVIOUS_TIME_LENGTH_SECONDS_ID= "com.example.timerapplication.previoustimerlength"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIME_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIME_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID= "com.example.timerapplication.timerstate"

        fun getTimerState(context: Context): MainActivity.TimerState{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal= preferences.getInt(TIMER_STATE_ID, 0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState, context: Context){
            val editor= PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal= state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID= "com.example.timerapplication.secondsremaining"

        fun getTimerSecondsRemaining(context: Context): Long{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setTimerSecondsRemaining(seconds: Long, context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID= "com.example.timerapplication.backgroundedtime"

        fun getAlarmSetTime(context: Context): Long{
            val preferences=PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor=PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}