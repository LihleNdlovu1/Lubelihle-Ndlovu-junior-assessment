package com.PersonaPulse.personapulse.notification

import com.PersonaPulse.personapulse.model.TodoData

interface INotificationManager {
    fun scheduleTaskReminder(todo: TodoData)
    fun scheduleOverdueCheck(todo: TodoData)
    fun scheduleDailySummary()
    fun cancelTaskReminder(todoId: String)
    fun cancelAllReminders()
    fun showImmediateNotification(todo: TodoData, type: String)
    fun showDailySummary(completedTasks: Int, pendingTasks: Int)
}
