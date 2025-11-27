package com.PersonaPulse.personapulse.notification

import com.PersonaPulse.personapulse.model.TodoData

interface INotificationService {
    fun showTaskReminderNotification(todo: TodoData)
    fun showOverdueTaskNotification(todo: TodoData)
    fun showDailySummaryNotification(completedTasks: Int, pendingTasks: Int)
    fun cancelNotification(notificationId: Int)
    fun cancelAllNotifications()
}
