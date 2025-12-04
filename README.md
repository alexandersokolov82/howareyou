# Friends CRM - Keep In Touch

A personal Android app to help you maintain regular contact with friends and acquaintances.

## Features

- **Contact Management**: Store friends' info (name, phone, Telegram, email, birthday)
- **Smart Reminders**: Individual notifications based on customizable intervals per person
- **Timeline View**: See upcoming reminders and birthdays at a glance
- **Filters**: View all contacts, birthdays only, or important contacts
- **Contact Actions**:
  - Mark as contacted (updates reminder automatically)
  - Snooze reminders (3 or 7 days)
  - Enable/disable reminders per contact
- **History Tracking**: Track all interactions with each friend
- **Importance Levels**: Tag contacts as low/normal/high priority
- **Data Backup**: Export/import your data as JSON
- **Phone Integration**:
  - Import contacts from phone
  - Click to call, email, or message via Telegram
- **Daily Notifications**: WorkManager-based reminders at your chosen time

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Data**: Kotlinx Serialization (JSON storage)
- **Notifications**: WorkManager
- **Navigation**: Jetpack Navigation Compose
- **Architecture**: MVVM with Repository pattern

## Project Structure

```
app/src/main/java/com/friendscrm/
├── data/
│   ├── model/          # Data classes (Contact, Settings, etc.)
│   └── repository/     # FriendsRepository (JSON read/write)
├── ui/
│   ├── screens/        # Composable screens
│   ├── theme/          # Material theme
│   ├── MainActivity.kt
│   ├── Navigation.kt
│   ├── TimelineViewModel.kt
│   └── ContactsImporter.kt
└── worker/             # WorkManager for notifications
```

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 34
- Gradle 8.2+
- Java 17

### Build Steps

1. **Clone or download this project**

2. **Open in Android Studio**
   - File → Open → Select the `howareyou` folder

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle dependencies
   - If not, click "Sync Project with Gradle Files"

4. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)

5. **Run on device/emulator**
   - Connect Android device (API 26+) or start emulator
   - Click Run (green play button)

### Debug Build

```bash
./gradlew assembleDebug
```

The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

## Previewing the App

- **Interactive preview (recommended)**: Open the project in Android Studio and run on an emulator or a physical device (API 26+). Click the green **Run** button to install and launch the app.
- **Compose Preview**: Open any composable file (e.g., `TimelineScreen.kt`) and use the **Split** view with **Design** → **Interactive** in Android Studio to see a static preview of the UI without installing.
- **Browser?**: The app is Android-native (Jetpack Compose). There is no web build or browser preview; use an emulator/device instead.

## Using the App

### First Launch

1. Grant notification permission (Android 13+)
2. Optionally import contacts from phone
3. Start adding friends manually or edit imported contacts

### Adding a Contact

1. Click the **+** button on timeline
2. Fill in name (required) and optional fields
3. Set reminder interval (5/7/10/14/30/60/90 days)
4. Choose importance level
5. Save

### Managing Reminders

- **Mark Contacted**: Updates last interaction date, schedules next reminder
- **Snooze**: Postpone reminder by 3 or 7 days
- **Toggle Reminders**: Enable/disable for individual contacts
- **Change Interval**: Edit contact to adjust reminder frequency

### Timeline View

- Shows all contacts sorted by upcoming reminders
- **Filters**:
  - All: All contacts
  - Birthdays: Only contacts with birthdays
  - Important: Only high-priority contacts
- Color-coded importance: 🔴 High, 🔵 Normal, ⚫ Low

### Settings

- **Default Interval**: For new contacts
- **Notification Time**: When daily reminders are sent
- **Export Backup**: Save all data to JSON file
- **Import Backup**: Restore from JSON file

### Notifications

- Daily check at your chosen time (default 19:00)
- Individual notification per overdue contact
- Tap notification to open contact details

## Data Storage

All data is stored locally in:
- **File**: `friends_crm.json` in app's internal storage
- **Format**: JSON (human-readable)
- **Backup**: Use Export function to save to external storage

### JSON Structure

```json
{
  "version": 1,
  "settings": {
    "defaultIntervalDays": 10,
    "defaultNotificationHour": 19,
    "defaultNotificationMinute": 0,
    "importedFromContacts": false
  },
  "contacts": [
    {
      "id": "uuid",
      "name": "John Doe",
      "phone": "+1234567890",
      "telegram": "@johndoe",
      "email": "john@example.com",
      "birthday": "1990-06-15",
      "importance": "normal",
      "type": "friend",
      "reminderIntervalDays": 10,
      "nextReminderDate": "2025-12-15",
      "lastInteractionDate": "2025-12-05",
      "remindersEnabled": true,
      "notes": "Met at conference",
      "history": [
        {
          "date": "2025-12-05",
          "type": "contacted",
          "comment": "Sent message"
        }
      ]
    }
  ]
}
```

## Permissions

- `READ_CONTACTS`: Import from phone contacts (optional)
- `POST_NOTIFICATIONS`: Show reminders (Android 13+)
- `SCHEDULE_EXACT_ALARM`: Precise daily notifications

## Troubleshooting

### Notifications not working

1. Check notification permission granted
2. Check Settings → Notification Time is set
3. Verify battery optimization not blocking WorkManager
4. Check Do Not Disturb settings

### Import contacts not working

1. Grant READ_CONTACTS permission
2. Check you have contacts in phone
3. Already imported contacts are skipped (by name)

### Data lost

- Use Export function regularly to backup
- Check `friends_crm.json` exists in app storage
- Restore from backup using Import function

## Future Enhancements (Not Implemented)

- Recurring events beyond birthdays
- Contact groups
- Statistics and insights
- Cloud sync
- Widget for home screen

## License

Personal project - free to use and modify

## Credits

Built with ❤️ for staying connected with friends
