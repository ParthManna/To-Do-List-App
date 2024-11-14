package com.example.todo_p

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmManagerBroadcast : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val taskId = intent.getLongExtra("taskId", -1)
        if (taskId == -1L) {
            Toast.makeText(context, "Error: Invalid Task ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Display a notification for the alarm
        showNotification(context, taskId)

        // Use PendingIntent to open AlarmPage
        val activityIntent = Intent(context, AlarmPage::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent.send()


        // Play an alarm sound
        val mediaPlayer = MediaPlayer.create(context, R.raw.dandadan_op)
        mediaPlayer?.apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    private fun showNotification(context: Context, taskId: Long) {
        val channelId = "todo_alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel, required for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task alarms"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent that opens AlarmPage when notification is tapped
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val task = db.todoDao().getTaskById(taskId)

            if (task != null) {
                withContext(Dispatchers.Main) {
                    val notification = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(task.title)
                        .setContentText(task.description)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build()

                    // Show the notification
                    notificationManager.notify(taskId.toInt(), notification)
                }
            }
        }
    }

}
