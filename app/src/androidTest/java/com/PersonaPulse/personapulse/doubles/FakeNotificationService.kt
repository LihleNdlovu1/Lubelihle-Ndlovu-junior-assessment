package com.PersonaPulse.personapulse.doubles

import android.content.Context
import com.PersonaPulse.personapulse.model.TodoData
import com.PersonaPulse.personapulse.notification.INotificationService


class FakeNotificationService(context: Context) : INotificationService {

    var reminderCalled = false
    var overdueCalled = false
    var summaryCalled = false

    override fun showTaskReminderNotification(todo: TodoData) {
        reminderCalled = true
    }

    override fun showOverdueTaskNotification(todo: TodoData) {
        overdueCalled = true
    }

    override fun showDailySummaryNotification(completed: Int, pending: Int) {
        summaryCalled = true
    }

    override fun cancelNotification(notificationId: Int) {
        TODO("Not yet implemented")
    }

    override fun cancelAllNotifications() {
        TODO("Not yet implemented")
    }
}
