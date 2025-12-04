package com.friendscrm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.friendscrm.data.model.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: Settings,
    onSave: (Settings) -> Unit,
    onBack: () -> Unit,
    onImportContacts: () -> Unit,
    onExport: () -> Unit,
    onImportBackup: () -> Unit
) {
    var interval by remember { mutableStateOf(settings.defaultIntervalDays.toString()) }
    var hour by remember { mutableStateOf(settings.defaultNotificationHour.toString()) }
    var minute by remember { mutableStateOf(settings.defaultNotificationMinute.toString()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = interval, onValueChange = { interval = it }, label = { Text("Default interval days") })
            OutlinedTextField(value = hour, onValueChange = { hour = it }, label = { Text("Notification hour (0-23)") })
            OutlinedTextField(value = minute, onValueChange = { minute = it }, label = { Text("Notification minute (0-59)") })
            Button(onClick = onImportContacts, modifier = Modifier.fillMaxWidth()) { Text("Import contacts from phone") }
            Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export JSON backup") }
            Button(onClick = onImportBackup, modifier = Modifier.fillMaxWidth()) { Text("Import JSON backup") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onSave(
                        settings.copy(
                            defaultIntervalDays = interval.toIntOrNull() ?: settings.defaultIntervalDays,
                            defaultNotificationHour = hour.toIntOrNull() ?: settings.defaultNotificationHour,
                            defaultNotificationMinute = minute.toIntOrNull() ?: settings.defaultNotificationMinute
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
