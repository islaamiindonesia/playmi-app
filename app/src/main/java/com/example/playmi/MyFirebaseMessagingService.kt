package com.example.playmi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var channelID: String

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        channelID = resources.getString(R.string.default_notification_channel_id)

        sendNotification(
            remoteMessage.notification?.title,
            remoteMessage.notification?.body
        )
    }

    private fun sendNotification(
        title: String?,
        body: String?,
        type: String? = null,
        id: Int? = null
    ) {
        var intent: Intent? = null

        if (!type.isNullOrEmpty()) {
            /*intent = when (type) {
                "News" -> Intent(this, NewsDetailActivity::class.java).apply {
                    putExtra(NewsDetailActivity.EXTRA_NEWS_ID, id)
                }
                "Promotion" -> Intent(this, PromotionDetailActivity::class.java).apply {
                    putExtra(PromotionDetailActivity.EXTRA_PROMOTION_ID, id)
                }
                "Pembiayaan" -> Intent(this, FinancingDetailActivity::class.java).apply {
                    putExtra(FinancingDetailActivity.EXTRA_MY_FINANCING_ID, id.toString())
                }
                else -> Intent(this, MainActivity::class.java)
            }*/
        }
        /*val pendingIntent = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            intent?.let { addNextIntentWithParentStack(it) }
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }*/

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            setSound(defaultSoundUri)
//            setContentIntent(pendingIntent)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelID,
                    "Notifikasi",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}