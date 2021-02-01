package com.amr.randomlyaskme

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    var catTitle : String = ""
    lateinit var questions: MutableList<Question>
    private fun createNotificationChannel(context: Context, name: String, description: String): String {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val chanelId = UUID.randomUUID().toString()
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(chanelId, name, importance)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.description = description
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        return chanelId
    }
    override fun onReceive(context: Context, intent: Intent?) {

        val db = dbHandler(context)

        val cat = intent?.getStringExtra("cat")

        if(cat != null && cat != "all"){
            catTitle = db.readSingleCategory(cat.toInt()).title
            questions = db.readCategoryQuestions(cat.toInt())
        } else if (cat == "all"){
        questions = db.readAllQuestions()
        } else {
            Toast.makeText(context, "selected category is null. We have a problem.", Toast.LENGTH_SHORT).show()
        }

        val thisquestion = questions.random()
        var datatosend = ""
        if (thisquestion.twoways){
            val notiset = arrayOf(thisquestion.answer, thisquestion.question)
            datatosend = notiset.random()
        } else {
            datatosend = thisquestion.question
        }
        val quID : String = (thisquestion.id).toString()

        val wwhen = System.currentTimeMillis()
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val yesButtonIntent = Intent(context,NotificationAction::class.java)
//        yesButtonIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_NEW_TASK)
        yesButtonIntent.apply {
            action = "YES"
            putExtra("state","yes")
            putExtra("questionID",quID)
        }

        val noButtonIntent = Intent(context,NotificationAction::class.java)
//        noButtonIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_NEW_TASK)
        noButtonIntent.apply {
            action = "NO"
            putExtra("state","no")
            putExtra("questionID",quID)
        }

        val yesPendingIntent = PendingIntent.getBroadcast(context,0,yesButtonIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val noPendingIntent = PendingIntent.getBroadcast(context,0,noButtonIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotifyBuilder = NotificationCompat.Builder(
            context, createNotificationChannel(context, "title", "body")
        ).setSmallIcon(R.drawable.ic_popup_reminder)
            .setContentText(datatosend)
            .setSound(alarmSound)
            .setAutoCancel(true).setWhen(wwhen)
            .addAction(R.drawable.ic_dialog_alert, "YES", yesPendingIntent)
            .addAction(R.drawable.ic_dialog_alert, "NO", noPendingIntent)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        if ( cat != "all" && cat != ""){
            mNotifyBuilder.setContentTitle("Do you know this one from $catTitle?")
        } else {
            mNotifyBuilder.setContentTitle("Do you know the answer to this one?")
        }

        notificationManager.notify(123, mNotifyBuilder.build())
    }
}