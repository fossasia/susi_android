package org.fossasia.susi.ai.Service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SusiFirebaseMessagingService : FirebaseMessagingService(){
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.data!=null){
            sendNotification(remoteMessage)
        }
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.e("FCMTOKEN","Token- "+ token)
    }
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title=remoteMessage.data["tittle"]
        val content =remoteMessage.data["content"]

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID="Susi"

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            val notificationChannel= NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Susi Notification",
                    NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.description="Susi Notification Channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor=(Color.RED)
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern= longArrayOf(0,1000,500,500)

            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder= NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.support.v4.R.drawable.notification_icon_background)
                .setContentTitle(title)
                .setContentText(content)
        notificationManager.notify(1,notificationBuilder.build())
    }
}