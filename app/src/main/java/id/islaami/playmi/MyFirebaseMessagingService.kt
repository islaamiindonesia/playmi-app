package id.islaami.playmi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.islaami.playmi.ui.MainActivity
import id.islaami.playmi.ui.video.VideoDetailActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var intent: Intent

    private var pendingIntent: PendingIntent? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // upon receiving notification
        handleNotification(
            remoteMessage.notification?.title.toString(),
            remoteMessage.notification?.body.toString(),
            remoteMessage.data
        )
    }

    private fun handleNotification(
        title: String,
        body: String,
        data: MutableMap<String, String>? = null
    ) {
        if (data != null) {
            // initialize intent with Extras or just an Intent to MainActivity class
            // INFO: For the data schema of the push notif, you can review them in the Core API's source code, inside "App/Notification" folder
            intent = when (data["type"]) {
                "VERIFICATION" -> Intent("SEND_INQUIRY_DATA").apply {
                    putExtra("VERIFICATION_CODE", data["code"])
                }
                "NEW_VIDEO" -> Intent(this, VideoDetailActivity::class.java).apply {
                    putExtra(VideoDetailActivity.EXTRA_ID, data["videoID"])
                }
                else -> Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }

            // when intent action is SEND_INQUIRY_DATA, system will broadcast the intent across activities inside the app
            // otherwise, it will just initialize pendingIntent with TaskStackBuilder
            if (intent.action == "SEND_INQUIRY_DATA") {
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            } else {
                pendingIntent = TaskStackBuilder.create(this).run {
                    // Add the intent, which inflates the back stack
                    addNextIntentWithParentStack(intent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }
        }

        displayNotification(title, body)
    }

    fun displayNotification(title: String, body: String) {
        val channelID = resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, channelID).apply {
            setSmallIcon(R.drawable.app_icon)
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            setSound(defaultSoundUri)
            if (pendingIntent != null) setContentIntent(pendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelID,
                    "Notifikasi Video",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}