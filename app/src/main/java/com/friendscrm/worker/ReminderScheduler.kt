package com.friendscrm.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.friendscrm.data.model.Settings
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "friends_crm_reminder_work"

    fun scheduleDaily(context: Context, settings: Settings) {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(settings.defaultNotificationHour, settings.defaultNotificationMinute)
        val nextRun = if (now.toLocalTime().isAfter(targetTime)) now.toLocalDate().plusDays(1).atTime(targetTime) else now.toLocalDate().atTime(targetTime)
        val delay = Duration.between(now, nextRun)

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.seconds, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
    }
}
