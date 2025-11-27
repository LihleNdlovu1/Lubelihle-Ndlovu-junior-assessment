package com.PersonaPulse.personapulse.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.PersonaPulse.personapulse.MainActivity
import com.PersonaPulse.personapulse.R
import com.PersonaPulse.personapulse.model.TodoData

class NotificationService(private val context: Context): INotificationService {
    
    companion object {
        const val CHANNEL_ID_TASK_REMINDER = "task_reminder"
        const val CHANNEL_ID_TASK_OVERDUE = "task_overdue"
        const val CHANNEL_ID_DAILY_SUMMARY = "daily_summary"
        
        const val NOTIFICATION_ID_TASK_REMINDER = 1000
        const val NOTIFICATION_ID_TASK_OVERDUE = 2000
        const val NOTIFICATION_ID_DAILY_SUMMARY = 3000
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Task Reminder Channel
            val taskReminderChannel = NotificationChannel(
                CHANNEL_ID_TASK_REMINDER,
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for upcoming task deadlines"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // Task Overdue Channel
            val taskOverdueChannel = NotificationChannel(
                CHANNEL_ID_TASK_OVERDUE,
                "Overdue Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for overdue tasks"
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
            }
            
            // Daily Summary Channel
            val dailySummaryChannel = NotificationChannel(
                CHANNEL_ID_DAILY_SUMMARY,
                "Daily Summary",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Daily productivity summary"
                enableVibration(false)
                setShowBadge(false)
            }
            
            notificationManager.createNotificationChannels(
                listOf(taskReminderChannel, taskOverdueChannel, dailySummaryChannel)
            )
        }
    }
    
    override fun showTaskReminderNotification(todo: TodoData) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            todo.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Task Reminder")
            .setContentText("${todo.title} is due soon!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${todo.title}\n${todo.description ?: "No description"}\nPriority: ${todo.priority.name}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(todo.id.hashCode(), notification)
        }
    }
    
    override fun showOverdueTaskNotification(todo: TodoData) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            todo.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASK_OVERDUE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⚠️ Task Overdue")
            .setContentText("${todo.title} is overdue!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${todo.title}\n${todo.description ?: "No description"}\nPriority: ${todo.priority.name}\nThis task was due and needs attention!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(todo.id.hashCode(), notification)
        }
    }
    
    override fun showDailySummaryNotification(completedTasks: Int, pendingTasks: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_DAILY_SUMMARY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY_SUMMARY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Daily Summary")
            .setContentText("You completed $completedTasks tasks today!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Great work! You completed $completedTasks tasks today.\n$pendingTasks tasks are still pending.\nKeep up the good work!"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_DAILY_SUMMARY, notification)
        }
    }
    
    override fun cancelNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }
    
    override fun cancelAllNotifications() {
        with(NotificationManagerCompat.from(context)) {
            cancelAll()
        }
    }
}



