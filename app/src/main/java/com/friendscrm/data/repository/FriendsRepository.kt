package com.friendscrm.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.friendscrm.data.model.AppData
import com.friendscrm.data.model.Contact
import com.friendscrm.data.model.HistoryItem
import com.friendscrm.data.model.Settings
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FriendsRepository(
    private val context: Context,
    private val fileName: String = "friends_crm.json"
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val formatter = DateTimeFormatter.ISO_DATE

    fun loadData(): AppData {
        return try {
            val input = context.openFileInput(fileName)
            val text = input.bufferedReader().use { it.readText() }
            json.decodeFromString<AppData>(text)
        } catch (e: Exception) {
            AppData()
        }
    }

    fun saveData(appData: AppData) {
        val text = json.encodeToString(appData)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(text.toByteArray())
        }
    }

    fun addOrUpdateContact(contact: Contact) {
        val data = loadData()
        val updated = data.contacts.filterNot { it.id == contact.id } + contact
        saveData(data.copy(contacts = updated))
    }

    fun deleteContact(id: String) {
        val data = loadData()
        saveData(data.copy(contacts = data.contacts.filterNot { it.id == id }))
    }

    fun markContacted(id: String, comment: String = "Contacted") {
        val data = loadData()
        val today = LocalDate.now()
        val updated = data.contacts.map { contact ->
            if (contact.id == id) {
                val interval = contact.reminderIntervalDays ?: data.settings.defaultIntervalDays
                val nextDate = today.plusDays(interval.toLong())
                contact.copy(
                    lastInteractionDate = formatter.format(today),
                    nextReminderDate = formatter.format(nextDate),
                    history = contact.history + HistoryItem(
                        date = formatter.format(today),
                        type = "contacted",
                        comment = comment
                    )
                )
            } else contact
        }
        saveData(data.copy(contacts = updated))
    }

    fun snoozeContact(id: String, days: Long) {
        val data = loadData()
        val updated = data.contacts.map { contact ->
            if (contact.id == id) {
                val baseDate = contact.nextReminderDate?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
                contact.copy(nextReminderDate = formatter.format(baseDate.plusDays(days)))
            } else contact
        }
        saveData(data.copy(contacts = updated))
    }

    fun toggleReminders(id: String, enabled: Boolean) {
        val data = loadData()
        val updated = data.contacts.map { contact ->
            if (contact.id == id) contact.copy(remindersEnabled = enabled) else contact
        }
        saveData(data.copy(contacts = updated))
    }

    fun updateSettings(settings: Settings) {
        val data = loadData()
        saveData(data.copy(settings = settings))
    }

    fun importContactsOnce(): AppData {
        val data = loadData()
        if (data.settings.importedFromContacts) return data

        val contacts = loadContactsFromDevice(context.contentResolver)
        val merged = (data.contacts + contacts).distinctBy { it.name.lowercase() }
        val newSettings = data.settings.copy(importedFromContacts = true)
        val updated = data.copy(contacts = merged, settings = newSettings)
        saveData(updated)
        return updated
    }

    private fun loadContactsFromDevice(contentResolver: ContentResolver): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        ) ?: return emptyList()

        cursor.use { c ->
            val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            while (c.moveToNext()) {
                val contactId = c.getString(idIndex)
                val name = c.getString(nameIndex) ?: continue
                val phone = loadPhone(contentResolver, contactId)
                val email = loadEmail(contentResolver, contactId)
                val birthday = loadBirthday(contentResolver, contactId)
                contacts.add(
                    Contact(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        phone = phone,
                        email = email,
                        birthday = birthday,
                        importance = "normal",
                        type = "friend",
                        reminderIntervalDays = null,
                        nextReminderDate = null,
                        lastInteractionDate = null,
                        notes = "Imported from contacts"
                    )
                )
            }
        }
        return contacts
    }

    private fun loadPhone(resolver: ContentResolver, contactId: String): String? {
        val phonesCursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?",
            arrayOf(contactId),
            null
        )
        phonesCursor?.use { cursor ->
            if (cursor.moveToFirst()) return cursor.getString(0)
        }
        return null
    }

    private fun loadEmail(resolver: ContentResolver, contactId: String): String? {
        val emailCursor = resolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID}=?",
            arrayOf(contactId),
            null
        )
        emailCursor?.use { cursor ->
            if (cursor.moveToFirst()) return cursor.getString(0)
        }
        return null
    }

    private fun loadBirthday(resolver: ContentResolver, contactId: String): String? {
        val where = "${ContactsContract.Data.MIMETYPE}=? AND ${ContactsContract.CommonDataKinds.Event.TYPE}=? AND ${ContactsContract.Data.CONTACT_ID}=?"
        val args = arrayOf(
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString(),
            contactId
        )
        val cursor = resolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE),
            where,
            args,
            null
        )
        cursor?.use { c ->
            if (c.moveToFirst()) return c.getString(0)
        }
        return null
    }

    fun exportToStream(outputStream: OutputStream) {
        val appData = loadData()
        val text = json.encodeToString(appData)
        outputStream.use { it.write(text.toByteArray()) }
    }

    fun importFromStream(inputStream: InputStream) {
        val text = inputStream.use { it.bufferedReader().readText() }
        val data = json.decodeFromString<AppData>(text)
        saveData(data)
    }
}
