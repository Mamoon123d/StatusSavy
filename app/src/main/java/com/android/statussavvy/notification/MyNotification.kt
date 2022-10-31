package com.android.statussavvy.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.android.statussavvy.R
import com.android.statussavvy.activity.Home
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyNotification : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message != null) {
            val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val title: String? = message.notification?.title
            val msg: String? = message.notification?.body
            //String CHANNEL_NAME = "Notification";
            //String CHANNEL_NAME = "Notification";
            val CHANNEL_ID = application.getString(R.string.app_name)

            //data key
            //val OFFER_ID_KEY = "OfferKey"

            //val offerId: String? = message.data[OFFER_ID_KEY]


            val intent = Intent(this, Home::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            @SuppressLint("UnspecifiedImmutableFlag")
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                121,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_NO_CREATE
            )
            val notificationManager = NotificationManagerCompat.from(this)
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.addLine(msg)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = getString(R.string.app_name)
                val description = "Notification"
                val channel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = description
                channel.enableVibration(true)
                channel.lightColor = Color.BLUE
                /*channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification),
                            *new AudioAttributes.Builder()
                                    *.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    *.setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    *.build());*/channel.setShowBadge(true)
                notificationManager.createNotificationChannel(channel)
            }


            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setVibrate(longArrayOf(0, 100))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setLights(Color.BLUE, 3000, 3000)
                    .setAutoCancel(false)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pendingIntent) //.setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(ContextCompat.getColor(this, R.color.second_color))
                    .setStyle(inboxStyle)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.mipmap.ic_launcher
                        )
                    )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(CHANNEL_ID)
            }

            notificationManager.notify(m, notificationBuilder.build())
        }

    }

}