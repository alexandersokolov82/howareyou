package com.friendscrm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val date: String, // YYYY-MM-DD
    val type: String, // "reminder_sent" / "contacted" / "birthday_congrats"
    val comment: String = ""
)
