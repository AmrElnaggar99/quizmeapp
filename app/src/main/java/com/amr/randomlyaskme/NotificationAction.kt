package com.amr.randomlyaskme

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationAction: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        val db = dbHandler(context)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
        val state = intent?.getStringExtra("state")
        val questionID = intent?.getStringExtra("questionID")
        if (questionID != null && questionID != "0"){
            val thisquestion : Question = db.readSingleQuestion((questionID.toInt()))
            thisquestion.revise = (state != "yes")
            db.updateQuestion(thisquestion)
//            val newquestion = db.readSingleQuestion(questionID.toInt())
//            Toast.makeText(context, "$state clicked. (${thisquestion.id}) (${questionID} / ${questionID.toInt()}) new question ${newquestion.question} ${newquestion.id} revise has been changed to ${newquestion.revise}", Toast.LENGTH_LONG).show()
        } else{
            Toast.makeText(context,"There was a problem processing this action." ,Toast.LENGTH_SHORT).show()
        }
        }

}