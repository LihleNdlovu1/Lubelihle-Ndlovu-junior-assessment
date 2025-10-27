package com.PersonaPulse.personapulse.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.model.Priority

class NotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationType = intent.getStringExtra("notification_type") ?: return
        
        when (notificationType) {
            "reminder" -> {
                val todoId = intent.getStringExtra("todo_id") ?: return
                val todoTitle = intent.getStringExtra("todo_title") ?: return
                val todoDescription = intent.getStringExtra("todo_description")
                
                val todo = TodoData(
                    id = todoId,
                    title = todoTitle,
                    description = todoDescription,
                    priority = Priority.MEDIUM // Default priority for scheduled notifications
                )
                
                val notificationManager = NotificationManager(context)
                notificationManager.showImmediateNotification(todo, "reminder")
            }
            
            "overdue" -> {
                val todoId = intent.getStringExtra("todo_id") ?: return
                val todoTitle = intent.getStringExtra("todo_title") ?: return
                val todoDescription = intent.getStringExtra("todo_description")
                
                val todo = TodoData(
                    id = todoId,
                    title = todoTitle,
                    description = todoDescription,
                    priority = Priority.HIGH // Overdue tasks are high priority
                )
                
                val notificationManager = NotificationManager(context)
                notificationManager.showImmediateNotification(todo, "overdue")
            }
            
            "daily_summary" -> {
                // For now, show a generic daily summary
                // In a real app, you'd fetch actual data from your database
                val notificationManager = NotificationManager(context)
                notificationManager.showDailySummary(3, 2) // Mock data
            }
        }
    }
}



