package com.friendscrm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.friendscrm.data.model.Contact
import com.friendscrm.data.model.HistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    contact: Contact,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkContacted: () -> Unit,
    onSnooze: (Long) -> Unit,
    onToggleReminders: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(contact.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ContactInfoSection(contact)
                ActionButtons(
                    onMarkContacted = onMarkContacted,
                    onSnooze = onSnooze,
                    remindersEnabled = contact.remindersEnabled,
                    onToggleReminders = onToggleReminders
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete Contact") }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            items(contact.history) { item ->
                HistoryRow(item)
            }
        }
    }
}

@Composable
private fun ContactInfoSection(contact: Contact) {
    Column(modifier = Modifier.padding(16.dp)) {
        contact.phone?.let { Text("Phone: $it", style = MaterialTheme.typography.bodyMedium) }
        contact.telegram?.let { Text("Telegram: $it", style = MaterialTheme.typography.bodyMedium) }
        contact.email?.let { Text("Email: $it", style = MaterialTheme.typography.bodyMedium) }
        contact.birthday?.let { Text("Birthday: $it", style = MaterialTheme.typography.bodyMedium) }
        contact.nextReminderDate?.let { Text("Next reminder: $it", style = MaterialTheme.typography.bodyMedium) }
        contact.lastInteractionDate?.let { Text("Last interaction: $it", style = MaterialTheme.typography.bodyMedium) }
        if (contact.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(contact.notes)
        }
    }
}

@Composable
private fun ActionButtons(
    onMarkContacted: () -> Unit,
    onSnooze: (Long) -> Unit,
    remindersEnabled: Boolean,
    onToggleReminders: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Button(onClick = onMarkContacted, modifier = Modifier.fillMaxWidth()) { Text("Contacted now") }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onSnooze(3) }, modifier = Modifier.weight(1f)) { Text("Snooze +3 days") }
            OutlinedButton(onClick = { onSnooze(7) }, modifier = Modifier.weight(1f)) { Text("Snooze +7 days") }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Reminders enabled", modifier = Modifier.weight(1f))
            Switch(checked = remindersEnabled, onCheckedChange = onToggleReminders)
        }
    }
}

@Composable
private fun HistoryRow(item: HistoryItem) {
    ListItem(
        headlineContent = { Text(item.type.replaceFirstChar { it.uppercaseChar() }) },
        supportingContent = { Text(item.comment) },
        overlineContent = { Text(item.date) }
    )
    Divider()
}
