package com.friendscrm.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.friendscrm.ui.theme.FriendsCRMTheme
import com.friendscrm.worker.ReminderScheduler

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            contentResolver.openOutputStream(it)?.use { stream -> viewModel.export(stream) }
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            contentResolver.openInputStream(it)?.use { stream -> viewModel.import(stream) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            FriendsCRMTheme {
                val navController = rememberNavController()
                val uiState by viewModel.uiState.collectAsState()
                LaunchedEffect(uiState.data.settings) {
                    ReminderScheduler.scheduleDaily(applicationContext, uiState.data.settings)
                }
                LaunchedEffect(intent?.getStringExtra("contactId")) {
                    intent?.getStringExtra("contactId")?.let { navController.navigate(Destinations.ContactDetails.create(it)) }
                }
                AppNavHost(
                    viewModel = viewModel,
                    navController = navController,
                    onExport = { exportJson() },
                    onImportBackup = { importJson() }
                )
            }
        }
    }

    override fun onNewIntent(newIntent: android.content.Intent?) {
        super.onNewIntent(newIntent)
        intent = newIntent
    }

    fun exportJson() = exportLauncher.launch("friends_crm_backup.json")
    fun importJson() = importLauncher.launch(arrayOf("application/json"))

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
