package com.friendscrm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val id: String,
    val name: String,
    val phone: String? = null,
    val telegram: String? = null,
    val email: String? = null,
    val birthday: String? = null, // YYYY-MM-DD
    val importance: String = "normal", // low / normal / high
    val type: String = "friend", // friend / debtor / i_owe
    val reminderIntervalDays: Int? = 10,
    val nextReminderDate: String? = null,
    val lastInteractionDate: String? = null,
    val remindersEnabled: Boolean = true,
    val notes: String = "",
    val history: List<HistoryItem> = emptyList()
)
