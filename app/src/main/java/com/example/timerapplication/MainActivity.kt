package com.example.timerapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.timerapplication.util.NotificationUtil
import com.example.timerapplication.util.PrefUtil

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime=(nowSeconds+secondsRemaining)*1000
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent= Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent=PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent=Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent=PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis/1000
    }


    enum class TimerState{
        STOPPED, PAUSED, RUNNING
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds= 0L
    private var timerState= TimerState.STOPPED

    private var secondsRemaining= 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title="       Timer"

        fabStart.setOnClickListener{v ->
            startTimer()
            timerState= TimerState.RUNNING
            updateButtons()
        }

        fabPause.setOnClickListener { v ->
            timer.cancel()
            timerState= TimerState.PAUSED
            updateButtons()
        }

        fabStop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        super.onResume()
        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if(timerState==TimerState.RUNNING){
            timer.cancel()
            val wakeUpTime= setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        }else if(timerState==TimerState.PAUSED){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setTimerSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initTimer(){
        timerState=PrefUtil.getTimerState(this)

        if (timerState==TimerState.STOPPED){
            setNewTimerLength()
        }else{
            setPreviousTimerLength()
        }

        secondsRemaining=if(timerState==TimerState.RUNNING || timerState==TimerState.PAUSED){
            PrefUtil.getTimerSecondsRemaining(this)
        }else{
            timerLengthSeconds
        }

        // TODO: Change secondsRemaining according to where the background timer stopped
        val alarmSetTime=PrefUtil.getAlarmSetTime(this)

        if(alarmSetTime>0){
            secondsRemaining-= nowSeconds-alarmSetTime
        }

        // resume where we left off
        if(secondsRemaining<=0){
            onTimerFinished()
        }else if(timerState==TimerState.RUNNING){
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState=TimerState.STOPPED

        setNewTimerLength()

        progressCountdown.progress=0
        PrefUtil.setTimerSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining=timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState=TimerState.RUNNING
        timer=object : CountDownTimer(secondsRemaining*1000, 1000){
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining=millisUntilFinished/1000
                updateCountdownUI()

            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes=PrefUtil.getTimerLength(this)
        timerLengthSeconds=lengthInMinutes*60L
        progressCountdown.max=timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds=PrefUtil.getPreviousTimerLengthSeconds(this)
        progressCountdown.max=timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished=secondsRemaining/60
        val secondsInMinutesUntilFinished=secondsRemaining-(minutesUntilFinished*60)
        val secondsString=secondsInMinutesUntilFinished.toString()
        textview_first.text="$minutesUntilFinished:${
        if(secondsString.length==2){secondsString}
        else{"0"+secondsString}}"
        progressCountdown.progress=(timerLengthSeconds-secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when(timerState){
            TimerState.RUNNING->{
                fabStart.isEnabled=false
                fabPause.isEnabled=true
                fabStop.isEnabled=true
            }
            TimerState.PAUSED->{
                fabStart.isEnabled=true
                fabPause.isEnabled=false
                fabStop.isEnabled=true
            }
            TimerState.STOPPED->{
                fabStart.isEnabled=true
                fabPause.isEnabled=false
                fabStop.isEnabled=false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent=Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
