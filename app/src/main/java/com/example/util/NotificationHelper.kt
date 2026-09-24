package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.model.NotificationPriority
import kotlin.random.Random

object NotificationHelper {
    const val CHANNEL_WORK_UPDATES = "avl_channel_work_updates"
    const val CHANNEL_CREW_DISPATCH = "avl_channel_crew_dispatch"
    const val CHANNEL_CALL_TIMES = "avl_channel_call_times"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val workChannel = NotificationChannel(
                CHANNEL_WORK_UPDATES,
                "AVL Work Updates & Gig Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates on new gigs, schedule shifts, and equipment alerts."
                enableVibration(true)
            }

            val dispatchChannel = NotificationChannel(
                CHANNEL_CREW_DISPATCH,
                "AVL Live Dispatch Comms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Priority stage announcements and crew broadcasts."
                enableVibration(true)
            }

            val callTimesChannel = NotificationChannel(
                CHANNEL_CALL_TIMES,
                "AVL Call-Time Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Upcoming call time reminders and load-in countdowns."
            }

            notificationManager.createNotificationChannel(workChannel)
            notificationManager.createNotificationChannel(dispatchChannel)
            notificationManager.createNotificationChannel(callTimesChannel)
        }
    }

    fun postWorkUpdateNotification(
        context: Context,
        title: String,
        message: String,
        priority: NotificationPriority = NotificationPriority.GENERAL,
        channelId: String = CHANNEL_WORK_UPDATES
    ) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // If not granted, still safe to return, in-app notification in Room will still display
                return
            }
        }

        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val priorityIcon = when (priority) {
                NotificationPriority.CRITICAL -> "🚨 [CRITICAL]"
                NotificationPriority.ALERT -> "⚠️ [HIGH]"
                NotificationPriority.LOGISTICS -> "🚚 [LOGISTICS]"
                NotificationPriority.GENERAL -> "ℹ️ [UPDATE]"
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("$priorityIcon $title")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(
                    if (priority == NotificationPriority.CRITICAL) NotificationCompat.PRIORITY_MAX
                    else NotificationCompat.PRIORITY_HIGH
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationId = Random.nextInt(1000, 999999)
            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            // Gracefully catch security/OS edge cases
            e.printStackTrace()
        }
    }
}
