package com.project.stageconnect.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.stageconnect.MainActivity
import com.project.stageconnect.R

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update("fcmToken", token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            showNotification(it.title.toString(), it.body.toString())
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel"
        val channelName = "Notifications StageConnect"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal pour notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("Notification", "Permission non accordée")
            return
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

    fun sendNotificationToUser(userId: String, title: String, body: String) {
        val data = hashMapOf(
            "uid" to userId,
            "title" to title,
            "body" to body
        )

        FirebaseFunctions.getInstance()
            .getHttpsCallable("sendNotificationToUser")
            .call(data)
            .addOnSuccessListener {
                Log.d("Notification", "Notification envoyée avec succès.")
            }
            .addOnFailureListener { e ->
                Log.e("Notification", "Erreur d'envoi de la notification : ${e.message}")
            }
    }
}
