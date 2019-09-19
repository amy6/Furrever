package com.example.furrever

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters


class RandomDogNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {

        Log.d("DOG", "Setting up notification")
        createNotification()

        return Result.success()
    }

    private fun createNotification() {
        val notificationManager =
            getSystemService(applicationContext, NotificationManager::class.java)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Default", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lightColor = R.color.holo_purple
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Default notification settings"
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Say hi to your dog")
            .setContentText("Seriously! Go say hello to your cute doggo pupper.")
            .setSmallIcon(R.drawable.star_on)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(createNotificationStyle())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(createPendingIntent())

        Log.d("DOG", "Displaying notification")
        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationStyle(): NotificationCompat.Style {
        return NotificationCompat.BigTextStyle()
            .bigText("This is like a big big text. You know. Like a paragraph. Where you don't have the 160 character limit. Or wait, may be there is..!")
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        return PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "id"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 1
    }
}