package com.PersonaPulse.personapulse.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.PersonaPulse.personapulse.model.TodoData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val notificationService = NotificationService(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    fun scheduleTaskReminder(todo: TodoData) {
        if (!checkNotificationPermission()) return
        
        val reminderTime = todo.dueDate?.let { dueDate ->
            // Schedule reminder 1 hour before due date
            dueDate - (60 * 60 * 1000) // 1 hour in milliseconds
        } ?: return
        
        if (reminderTime <= System.currentTimeMillis()) return
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_description", todo.description)
            putExtra("notification_type", "reminder")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }
    
    fun scheduleOverdueCheck(todo: TodoData) {
        if (!checkNotificationPermission()) return
        
        val overdueTime = todo.dueDate ?: return
        
        if (overdueTime <= System.currentTimeMillis()) {
            // Task is already overdue, show notification immediately
            notificationService.showOverdueTaskNotification(todo)
            return
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_description", todo.description)
            putExtra("notification_type", "overdue")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.hashCode() + 10000, // Different ID for overdue check
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                overdueTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                overdueTime,
                pendingIntent
            )
        }
    }
    
    fun scheduleDailySummary() {
        if (!checkNotificationPermission()) return
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // 8 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If it's already past 8 PM today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_type", "daily_summary")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID_DAILY_SUMMARY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
    
    fun cancelTaskReminder(todoId: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        
        // Also cancel overdue check
        val overdueIntent = Intent(context, NotificationReceiver::class.java)
        val overduePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId.hashCode() + 10000,
            overdueIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(overduePendingIntent)
    }
    
    fun cancelAllReminders() {
        // This would need to be implemented based on your todo storage
        // For now, we'll just cancel the daily summary
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID_DAILY_SUMMARY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    fun showImmediateNotification(todo: TodoData, type: String) {
        when (type) {
            "reminder" -> notificationService.showTaskReminderNotification(todo)
            "overdue" -> notificationService.showOverdueTaskNotification(todo)
        }
    }
    
    fun showDailySummary(completedTasks: Int, pendingTasks: Int) {
        notificationService.showDailySummaryNotification(completedTasks, pendingTasks)
    }
    
    companion object {
        const val NOTIFICATION_ID_DAILY_SUMMARY = 3000
    }
}



