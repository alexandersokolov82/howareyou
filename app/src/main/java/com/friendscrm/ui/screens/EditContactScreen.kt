package com.friendscrm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.friendscrm.data.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    initial: Contact,
    onSave: (Contact) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initial.name)) }
    var phone by remember { mutableStateOf(TextFieldValue(initial.phone.orEmpty())) }
    var telegram by remember { mutableStateOf(TextFieldValue(initial.telegram.orEmpty())) }
    var email by remember { mutableStateOf(TextFieldValue(initial.email.orEmpty())) }
    var birthday by remember { mutableStateOf(TextFieldValue(initial.birthday.orEmpty())) }
    var importance by remember { mutableStateOf(initial.importance) }
    var type by remember { mutableStateOf(initial.type) }
    var interval by remember { mutableStateOf((initial.reminderIntervalDays ?: 10).toString()) }
    var nextReminder by remember { mutableStateOf(TextFieldValue(initial.nextReminderDate.orEmpty())) }
    var lastInteraction by remember { mutableStateOf(TextFieldValue(initial.lastInteractionDate.orEmpty())) }
    var notes by remember { mutableStateOf(TextFieldValue(initial.notes)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (initial.id.isEmpty()) "Add Contact" else "Edit Contact") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telegram, onValueChange = { telegram = it }, label = { Text("Telegram") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = birthday, onValueChange = { birthday = it }, label = { Text("Birthday (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            IntervalDropdown(interval) { interval = it }
            ImportanceDropdown(importance) { importance = it }
            TypeDropdown(type) { type = it }
            OutlinedTextField(value = nextReminder, onValueChange = { nextReminder = it }, label = { Text("Next reminder date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lastInteraction, onValueChange = { lastInteraction = it }, label = { Text("Last interaction date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val updated = initial.copy(
                        name = name.text,
                        phone = phone.text.ifEmpty { null },
                        telegram = telegram.text.ifEmpty { null },
                        email = email.text.ifEmpty { null },
                        birthday = birthday.text.ifEmpty { null },
                        importance = importance,
                        type = type,
                        reminderIntervalDays = interval.toIntOrNull() ?: 10,
                        nextReminderDate = nextReminder.text.ifEmpty { null },
                        lastInteractionDate = lastInteraction.text.ifEmpty { null },
                        notes = notes.text
                    )
                    onSave(updated)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}

@Composable
private fun IntervalDropdown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("5", "10", "30", "60", "90")
    ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
        OutlinedTextField(
            value = selected,
            onValueChange = onSelected,
            label = { Text("Reminder interval (days)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ImportanceDropdown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("low", "normal", "high")
    ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
        OutlinedTextField(
            value = selected,
            onValueChange = onSelected,
            label = { Text("Importance") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TypeDropdown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("friend", "debtor", "i_owe")
    ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
        OutlinedTextField(
            value = selected,
            onValueChange = onSelected,
            label = { Text("Type") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
