package com.friendscrm.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.friendscrm.R
import com.friendscrm.data.model.Contact
import com.friendscrm.data.repository.FriendsRepository
import com.friendscrm.ui.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repository = FriendsRepository(appContext)
    private val formatter = DateTimeFormatter.ISO_DATE

    override suspend fun doWork(): Result {
        val data = repository.loadData()
        val today = LocalDate.now()
        createChannel()
        data.contacts.filter { contact ->
            contact.remindersEnabled && contact.nextReminderDate?.let { LocalDate.parse(it, formatter) <= today } == true
        }.forEachIndexed { index, contact ->
            showNotification(contact, index)
        }
        return Result.success()
    }

    private fun showNotification(contact: Contact, id: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("contactId", contact.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = androidx.core.app.TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(id, android.app.PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE else 0))
        }
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to reach out")
            .setContentText(contact.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        NotificationManagerCompat.from(applicationContext).notify(id, builder.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Keep In Touch"
            val descriptionText = "Friend reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "friends_crm_reminders"
    }
}
