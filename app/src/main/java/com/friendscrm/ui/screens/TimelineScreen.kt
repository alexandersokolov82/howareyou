package com.friendscrm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.friendscrm.data.model.Contact
import com.friendscrm.ui.AppUiState
import com.friendscrm.ui.TimelineFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    state: kotlinx.coroutines.flow.StateFlow<AppUiState>,
    onContactClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterChanged: (TimelineFilter) -> Unit
) {
    val uiState by state.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Keep In Touch") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterChipsRow(selected = uiState.filter, onFilterChanged = onFilterChanged)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.filteredContacts) { contact ->
                    ContactListItem(contact = contact, onClick = { onContactClick(contact.id) })
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(selected: TimelineFilter, onFilterChanged: (TimelineFilter) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipOption("All", selected == TimelineFilter.ALL) { onFilterChanged(TimelineFilter.ALL) }
        FilterChipOption("Birthdays", selected == TimelineFilter.BIRTHDAYS) { onFilterChanged(TimelineFilter.BIRTHDAYS) }
        FilterChipOption("Important", selected == TimelineFilter.IMPORTANT) { onFilterChanged(TimelineFilter.IMPORTANT) }
    }
}

@Composable
private fun FilterChipOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
fun ContactListItem(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = contact.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                if (contact.importance == "high") {
                    AssistChip(onClick = {}, label = { Text("High") })
                }
            }
            contact.nextReminderDate?.let { Text("Next reminder: $it", style = MaterialTheme.typography.bodySmall) }
            contact.birthday?.let { Text("Birthday: $it", style = MaterialTheme.typography.bodySmall) }
            if (!contact.notes.isNullOrBlank()) {
                Text(text = contact.notes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
