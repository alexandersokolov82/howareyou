package com.friendscrm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val defaultIntervalDays: Int = 10,
    val defaultNotificationHour: Int = 19,
    val defaultNotificationMinute: Int = 0,
    val importedFromContacts: Boolean = false
)

@Serializable
data class AppData(
    val version: Int = 1,
    val settings: Settings = Settings(),
    val contacts: List<Contact> = emptyList()
)
