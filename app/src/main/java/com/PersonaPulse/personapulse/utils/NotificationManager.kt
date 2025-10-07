package com.PersonaPulse.personapulse.utils

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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskNotificationManager(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "task_reminders"
        private const val CHANNEL_NAME = "Task Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for tasks due today"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showTaskDueNotification(task: TodoData) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val priorityText = when (task.priority) {
            com.PersonaPulse.personapulse.model.Priority.HIGH -> "High Priority"
            com.PersonaPulse.personapulse.model.Priority.MEDIUM -> "Medium Priority"
            com.PersonaPulse.personapulse.model.Priority.LOW -> "Low Priority"
            com.PersonaPulse.personapulse.model.Priority.OVERDUE -> "Overdue"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Due Today: ${task.title}")
            .setContentText("$priorityText • Due today")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${task.title}\n$priorityText\nDue today"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(task.id.hashCode(), notification)
        }
    }
    
    fun showMultipleTasksNotification(tasks: List<TodoData>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val taskList = tasks.take(3).joinToString("\n") { "• ${it.title}" }
        val moreText = if (tasks.size > 3) "\n... and ${tasks.size - 3} more" else ""
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${tasks.size} Tasks Due Today")
            .setContentText("You have ${tasks.size} task${if (tasks.size == 1) "" else "s"} due today")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tasks due today:\n$taskList$moreText"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(0, notification)
        }
    }
    
    fun cancelNotification(taskId: String) {
        with(NotificationManagerCompat.from(context)) {
            cancel(taskId.hashCode())
        }
    }
    
    fun cancelAllNotifications() {
        with(NotificationManagerCompat.from(context)) {
            cancelAll()
        }
    }
}

