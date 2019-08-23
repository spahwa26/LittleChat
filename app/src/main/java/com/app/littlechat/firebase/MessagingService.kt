package com.app.littlechat.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.littlechat.FriendRequests
import com.app.littlechat.HomeScreen
import com.app.littlechat.R
import com.app.littlechat.utility.CommonUtilities
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.media.RingtoneManager


class MessagingService : FirebaseMessagingService() {
    private var mNotificationManager: NotificationManager? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Log.d("", "From: ${remoteMessage?.from}")

        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d("", "Message data payload: " + remoteMessage.data)
            if (remoteMessage.data.containsKey("email"))
                showNotification(remoteMessage, false)
            else
                showNotification(remoteMessage, true)
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage, isMessage: Boolean) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var id = 1001
        var intent: Intent

        if (isMessage) {
            intent = Intent(applicationContext, HomeScreen::class.java)
        } else {
            id = (0..1000).random()
            intent = Intent(applicationContext, FriendRequests::class.java)
        }


        val mBuilder = NotificationCompat.Builder(applicationContext, "notify_001")
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.chat_small)
                .setLargeIcon(bitmap)
                .setContentTitle("Friend Request")
                .setContentText(remoteMessage.data.get("name") + " has sent you s friend request.")
                .setSound(sound)
                .setAutoCancel(true)

        if (isMessage) {
            mBuilder.setGroup("My Group")
            val bigText = NotificationCompat.InboxStyle()
            bigText.setBigContentTitle(remoteMessage.data["name"])
            bigText.addLine(remoteMessage.data["message"])
            mBuilder.setStyle(bigText)
        }

        mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= O) {
            val channelId = "notify_001"
            val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager!!.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager!!.notify("Little Chat", id, mBuilder.build())
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        CommonUtilities.putToken(applicationContext, token)
        Log.e("token", token)
    }

}