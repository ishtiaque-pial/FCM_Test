package com.example.fcmtest


import android.app.Notification
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
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
        showNotification(remoteMessage)

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
                 this@LocalFirebaseMessagingService, 0,
                 notificationIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE else 0
             )
            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    private fun initNotificationChannel() {
        val value ="/raw/cutom_rington_1"
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