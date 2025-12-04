package com.friendscrm.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.friendscrm.data.model.Contact
import com.friendscrm.ui.screens.ContactDetailsScreen
import com.friendscrm.ui.screens.EditContactScreen
import com.friendscrm.ui.screens.SettingsScreen
import com.friendscrm.ui.screens.TimelineScreen

sealed class Destinations(val route: String) {
    object Timeline : Destinations("timeline")
    object ContactDetails : Destinations("details/{contactId}") {
        fun create(id: String) = "details/$id"
    }
    object EditContact : Destinations("edit/{contactId}") {
        fun create(id: String) = "edit/$id"
    }
    object AddContact : Destinations("add")
    object Settings : Destinations("settings")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onExport: () -> Unit = {},
    onImportBackup: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = Destinations.Timeline.route, modifier = modifier) {
        composable(Destinations.Timeline.route) {
            TimelineScreen(
                state = viewModel.uiState,
                onContactClick = { navController.navigate(Destinations.ContactDetails.create(it)) },
                onAddClick = { navController.navigate(Destinations.AddContact.route) },
                onSettingsClick = { navController.navigate(Destinations.Settings.route) },
                onFilterChanged = { viewModel.setFilter(it) }
            )
        }
        composable(Destinations.ContactDetails.route) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: return@composable
            val contact = viewModel.uiState.value.data.contacts.find { it.id == contactId }
            contact?.let {
                ContactDetailsScreen(
                    contact = it,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Destinations.EditContact.create(contactId)) },
                    onDelete = {
                        viewModel.deleteContact(contactId)
                        navController.popBackStack()
                    },
                    onMarkContacted = { viewModel.markContacted(contactId) },
                    onSnooze = { days -> viewModel.snoozeContact(contactId, days) },
                    onToggleReminders = { enabled -> viewModel.toggleReminders(contactId, enabled) }
                )
            }
        }
        composable(Destinations.EditContact.route) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId") ?: return@composable
            val contact = viewModel.uiState.value.data.contacts.find { it.id == contactId }
            contact?.let {
                EditContactScreen(
                    initial = it,
                    onSave = { updated ->
                        viewModel.updateContact(updated)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(Destinations.AddContact.route) {
            EditContactScreen(
                initial = Contact(id = "", name = "", reminderIntervalDays = viewModel.uiState.value.data.settings.defaultIntervalDays),
                onSave = { contact ->
                    viewModel.addContact(contact)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.Settings.route) {
            SettingsScreen(
                settings = viewModel.uiState.value.data.settings,
                onSave = {
                    viewModel.updateSettings(it)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                onImportContacts = { viewModel.importFromContacts() },
                onExport = onExport,
                onImportBackup = onImportBackup
            )
        }
    }
}
