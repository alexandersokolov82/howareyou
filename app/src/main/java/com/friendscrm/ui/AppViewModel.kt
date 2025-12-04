package com.friendscrm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.friendscrm.data.model.AppData
import com.friendscrm.data.model.Contact
import com.friendscrm.data.model.Settings
import com.friendscrm.data.repository.FriendsRepository
import com.friendscrm.util.DateUtils
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TimelineFilter { ALL, BIRTHDAYS, IMPORTANT }

data class AppUiState(
    val data: AppData = AppData(),
    val filter: TimelineFilter = TimelineFilter.ALL
) {
    val filteredContacts: List<Contact>
        get() = when (filter) {
            TimelineFilter.ALL -> data.contacts
            TimelineFilter.BIRTHDAYS -> data.contacts.filter { !it.birthday.isNullOrEmpty() }
            TimelineFilter.IMPORTANT -> data.contacts.filter { it.importance == "high" }
        }.sortedWith(compareBy({ it.nextReminderDate ?: "9999-12-31" }, { it.birthday ?: "9999-12-31" }))
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FriendsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AppUiState(repository.loadData()))
    val uiState: StateFlow<AppUiState> = _uiState

    fun setFilter(filter: TimelineFilter) {
        _uiState.value = _uiState.value.copy(filter = filter)
    }

    fun addContact(template: Contact) {
        val contact = template.copy(
            id = template.id.ifBlank { UUID.randomUUID().toString() },
            reminderIntervalDays = template.reminderIntervalDays ?: _uiState.value.data.settings.defaultIntervalDays,
            nextReminderDate = template.nextReminderDate ?: DateUtils.format(DateUtils.today().plusDays((template.reminderIntervalDays ?: _uiState.value.data.settings.defaultIntervalDays).toLong()))
        )
        repository.addOrUpdateContact(contact)
        refresh()
    }

    fun updateContact(contact: Contact) {
        repository.addOrUpdateContact(contact)
        refresh()
    }

    fun deleteContact(id: String) {
        repository.deleteContact(id)
        refresh()
    }

    fun markContacted(id: String) {
        repository.markContacted(id)
        refresh()
    }

    fun snoozeContact(id: String, days: Long) {
        repository.snoozeContact(id, days)
        refresh()
    }

    fun toggleReminders(id: String, enabled: Boolean) {
        repository.toggleReminders(id, enabled)
        refresh()
    }

    fun updateSettings(settings: Settings) {
        repository.updateSettings(settings)
        refresh()
    }

    fun importFromContacts() {
        viewModelScope.launch {
            repository.importContactsOnce()
            refresh()
        }
    }

    fun export(outputStream: java.io.OutputStream) {
        repository.exportToStream(outputStream)
    }

    fun import(inputStream: java.io.InputStream) {
        repository.importFromStream(inputStream)
        refresh()
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(data = repository.loadData())
    }
}
