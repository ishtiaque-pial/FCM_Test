package com.example.fcmtest


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class LocalFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("fdgfd", "" + remoteMessage.data.toString())
        removeBrokenChannel()
        initNotificationChannel()
        generateNotification(remoteMessage.data["title"] ?: "title")
        //showNotification(remoteMessage)

    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(remoteMessage.data["title"] ?: "title")
            setContentText(remoteMessage.data["body"] ?: "body")
            priority = NotificationCompat.PRIORITY_HIGH
            setSound(
                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this@LocalFirebaseMessagingService.packageName}/${R.raw.cutom_rington_1}")
            )
            val bigTextStyle = NotificationCompat.BigTextStyle()
            bigTextStyle.setBigContentTitle(remoteMessage.data["title"] ?: "title")
            bigTextStyle.bigText(remoteMessage.data["body"] ?: "body")
            setStyle(bigTextStyle)
            setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            setAutoCancel(true)

            val notificationIntent =
                Intent(this@LocalFirebaseMessagingService, MainActivity::class.java)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                this@LocalFirebaseMessagingService,
                0,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else 0
            )
            setContentIntent(pendingIntent)
            setContent(getRemoteView(remoteMessage.data["title"] ?: "title"))
        }


        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    fun generateNotification(message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_ONE_SHOT)

        //CHANNEL ID,NAME

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_tiger)
                .setSound(
                    Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this@LocalFirebaseMessagingService.packageName}/${R.raw.cutom_rington_1}")
                )
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)


        builder = builder.setContent(getRemoteView(message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, this.packageName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(Random.nextInt(), builder.build())

        //MediaPlayer.create(applicationContext, R.raw.tone).start()

    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(message: String): RemoteViews {

        val remoteView = RemoteViews(
            this.packageName,
            R.layout.notification
        )


        //remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.description, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.ic_tiger)

        return remoteView
    }

    private fun initNotificationChannel() {
        val value = "/raw/cutom_rington_1"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelName = getString(R.string.general_channel_title)
        val channelDescription = getString(R.string.general_channel_description)
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance).apply {
            setName(channelName)
            setDescription(channelDescription)
            setSound(
                Uri.parse(
                    "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this@LocalFirebaseMessagingService.packageName}$value"
                ),
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            ) //after changing the ringtone you got to change the CHANNEL_ID as well to see the new ringtone effect
        }
        NotificationManagerCompat.from(this).createNotificationChannel(channel.build())
    }


    private fun removeBrokenChannel() {
        NotificationManagerCompat.from(this)
            .deleteNotificationChannel(BROKEN_CHANNEL_ID)
    }

    companion object {
        const val BROKEN_CHANNEL_ID: String = "general_channel_new"
        const val CHANNEL_ID: String = "general_channel_new_qwert"
    }

}