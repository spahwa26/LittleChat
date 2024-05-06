package com.app.littlechat.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {
    private var mNotificationManager: NotificationManager? = null

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("", "From: ${message.from}")

        // Check if message contains a data payload.
        message.data.isNotEmpty().let {
            Log.d("", "Message data payload: " + message.data)
            if (message.data.containsKey("email")) showNotification(message, false)
            else showNotification(message, true)
        }
    }

    private fun showNotification(message: RemoteMessage, isMessage: Boolean) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var id = 1001
        val intent: Intent

        if (isMessage) {
            //todo: handle chat messages
            intent = Intent(applicationContext, HomeActivity::class.java)
        } else {
            id = (0..1000).random()
            intent = Intent(applicationContext, HomeActivity::class.java)
            //todo: handle friend requests
        }


        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, FLAG_IMMUTABLE)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.chat_small).setLargeIcon(bitmap)
            .setContentTitle("Friend Request")
            .setContentText(message.data["name"] + " has sent you s friend request.")
            .setSound(sound).setAutoCancel(true)

        if (isMessage) {
            mBuilder.setGroup("My Group")
            val bigText = NotificationCompat.InboxStyle()
            bigText.setBigContentTitle(message.data["name"])
            bigText.addLine(message.data["message"])
            mBuilder.setStyle(bigText)
        }

        mNotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= O) {
            val channelId = "notify_001"
            val channel = NotificationChannel(
                channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager!!.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager!!.notify("Little Chat", id, mBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        UserPreferences(applicationContext).deviceToken = token
        Log.e("token", token)
    }

}