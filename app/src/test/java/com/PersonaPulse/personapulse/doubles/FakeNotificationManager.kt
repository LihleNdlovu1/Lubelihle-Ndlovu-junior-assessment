package com.PersonaPulse.personapulse.doubles

import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.notification.INotificationManager

class FakeNotificationManager : INotificationManager {

    val remindersScheduled = mutableListOf<TodoData>()
    val overdueChecksScheduled = mutableListOf<TodoData>()

    var dailySummaryScheduled = false

    val cancelledIds = mutableListOf<String>()

    var immediateNotifications = mutableListOf<Pair<TodoData, String>>()

    var summaryNotifications = mutableListOf<Pair<Int, Int>>()

    override fun scheduleTaskReminder(todo: TodoData) {
        remindersScheduled.add(todo)
    }

    override fun scheduleOverdueCheck(todo: TodoData) {
        overdueChecksScheduled.add(todo)
    }

    override fun scheduleDailySummary() {
        dailySummaryScheduled = true
    }

    override fun cancelTaskReminder(todoId: String) {
        cancelledIds.add(todoId)
    }

    override fun cancelAllReminders() {
        // For tests you can just mark a flag if needed
    }

    override fun showImmediateNotification(todo: TodoData, type: String) {
        immediateNotifications.add(todo to type)
    }

    override fun showDailySummary(completedTasks: Int, pendingTasks: Int) {
        summaryNotifications.add(completedTasks to pendingTasks)
    }
}
