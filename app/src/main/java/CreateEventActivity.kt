package com.example.derk
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material3.AlertDialog
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.derk.ui.theme.DerkTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val CreateScreenBackground = Color(0xFF121212)
private val CreatePanelBackground = Color(0xFF1E1E1E)
private val CreatePrimaryAction = Color(0xFF64B5F6)
private val CreateSecondaryAction = Color(0xFF263238)
private val CreateDestructiveAction = Color(0xFFE57373)
private val CreatePrimaryText = Color(0xFFF5F5F5)
private val CreateSecondaryText = Color(0xFFCFD8DC)
private val CreateBorderColor = Color(0xFF90A4AE)
private val CreateErrorColor = Color(0xFFFF8A80)
private val CreateDialogBackground = Color(0xFF1E1E1E)

class CreateEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val volunteerIndex = intent.getIntExtra("volunteerIndex", -1)

        setContent {
            DerkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CreateScreenBackground
                ) {
                    CreateEventScreen(volunteerIndex = volunteerIndex)
                }
            }
        }
    }
}
private fun isValidPhone(phone: String): Boolean {
    return phone.isNotBlank() && phone.all { it.isDigit() }
}

private fun hasValidDomain(email: String): Boolean {
    val trimmedEmail = email.trim()
    val parts = trimmedEmail.split("@")

    if (parts.size != 2) return false
    if (parts[0].isBlank()) return false

    val domain = parts[1]

    return domain.contains(".") &&
            !domain.startsWith(".") &&
            !domain.endsWith(".") &&
            !domain.contains("..")
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(volunteerIndex: Int = -1) {
    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current

    val existingVolunteer = if (volunteerIndex >= 0) VolunteerStore.get(volunteerIndex) else null
    val isEditing = existingVolunteer != null

    var showPopup by remember { mutableStateOf(false) }
    var popupAction by remember { mutableStateOf("") }
    var showInvalidEmailDialog by remember { mutableStateOf(false) }
    var showInvalidPhoneDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showNotificationPopup by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    var initialSelectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var formError by remember { mutableStateOf("") }

    LaunchedEffect(existingVolunteer) {
        if (existingVolunteer != null) {
            name = existingVolunteer.name
            email = existingVolunteer.email
            phone = existingVolunteer.phone
            eventName = existingVolunteer.eventName
            role = existingVolunteer.role
            notes = existingVolunteer.notes
            selectedDate = existingVolunteer.date
            selectedTime = existingVolunteer.time
            notificationsEnabled = existingVolunteer.notificationsEnabled

            initialSelectedDateMillis = existingVolunteer.date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    val dateDisplay = selectedDate?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "Select date"
    val timeDisplay = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select time"
    val currentSelectedTime by rememberUpdatedState(selectedTime)
    val scrollState = rememberScrollState()

    fun buildCurrentVolunteerOrNull(): Volunteer? {
        val finalDate = selectedDate ?: return null
        val finalTime = selectedTime ?: return null

        if (
            name.isBlank() ||
            email.isBlank() ||
            phone.isBlank() ||
            eventName.isBlank() ||
            role.isBlank()
        ) {
            return null
        }

        return Volunteer(
            name = name,
            email = email.trim(),
            phone = phone.trim(),
            eventName = eventName,
            role = role,
            notes = notes,
            date = finalDate,
            time = finalTime,
            notificationsEnabled = notificationsEnabled
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreateScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = if (isEditing) "Edit Event" else "Create Event",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp)
                    .semantics { heading() },
                color = CreatePrimaryText,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = CreatePanelBackground,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            formError = ""
                        },
                        label = { Text("Name") },
                        placeholder = { Text("Enter volunteer name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = formError.isNotBlank() && name.isBlank(),
                        colors = accessibleTextFieldColors()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            formError = ""
                        },
                        label = { Text("Email") },
                        placeholder = { Text("Enter email address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused && email.isNotBlank() && !hasValidDomain(email)) {
                                    showInvalidEmailDialog = true
                                }
                            },
                        singleLine = true,
                        isError = formError.isNotBlank() && email.isBlank(),
                        colors = accessibleTextFieldColors()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            formError = ""
                        },
                        label = { Text("Phone") },
                        placeholder = { Text("Enter phone number") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused && phone.isNotBlank() && !isValidPhone(phone)) {
                                    showInvalidPhoneDialog = true
                                }
                            },
                        singleLine = true,
                        isError = formError.isNotBlank() && phone.isBlank(),
                        colors = accessibleTextFieldColors()
                    )

                    OutlinedTextField(
                        value = eventName,
                        onValueChange = {
                            eventName = it
                            formError = ""
                        },
                        label = { Text("Event name") },
                        placeholder = { Text("Enter event name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = formError.isNotBlank() && eventName.isBlank(),
                        colors = accessibleTextFieldColors()
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = {
                            role = it
                            formError = ""
                        },
                        label = { Text("Role") },
                        placeholder = { Text("Enter volunteer role") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = formError.isNotBlank() && role.isBlank(),
                        colors = accessibleTextFieldColors()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                            formError = ""
                        },
                        label = { Text("Notes") },
                        placeholder = { Text("Optional notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { event ->
                                if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                                    focusManager.moveFocus(
                                        if (event.isShiftPressed) FocusDirection.Previous
                                        else FocusDirection.Next
                                    )
                                    true
                                } else {
                                    false
                                }
                            },
                        minLines = 3,
                        colors = accessibleTextFieldColors()
                    )

                    Button(
                        onClick = {
                            formError = ""
                            showDatePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreateSecondaryAction,
                            contentColor = CreatePrimaryText
                        )
                    ) {
                        Text("Date: $dateDisplay")
                    }

                    Button(
                        onClick = {
                            formError = ""
                            showTimePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreateSecondaryAction,
                            contentColor = CreatePrimaryText
                        )
                    ) {
                        Text("Time: $timeDisplay")
                    }

                    Button(
                        onClick = {
                            showNotificationPopup = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreateSecondaryAction,
                            contentColor = CreatePrimaryText
                        )
                    ) {
                        Text("Event Notifications: " + if (notificationsEnabled) "On" else "Off")
                    }

                    Button(
                        onClick = {
                            val volunteer = buildCurrentVolunteerOrNull()
                            if (volunteer == null) {
                                formError = "Fill in all required fields, date, and time before adding to calendar."
                            } else {
                                EventShareUtils.openCalendarInsert(context, volunteer)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Add event to local calendar"
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreatePrimaryAction,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Add to Calendar")
                    }

                    Button(
                        onClick = {
                            val volunteer = buildCurrentVolunteerOrNull()
                            if (volunteer == null) {
                                formError = "Fill in all required fields, date, and time before sharing by email."
                            } else {
                                EventShareUtils.openEmailShare(context, volunteer)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Share event by email"
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreatePrimaryAction,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Share by Email")
                    }

                    if (formError.isNotBlank()) {
                        Text(
                            text = formError,
                            color = CreateErrorColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Assertive
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        popupAction = "submit"
                        showPopup = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CreatePrimaryAction,
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (isEditing) "Save Changes" else "Submit")
                }

                if (isEditing) {
                    Button(
                        onClick = {
                            popupAction = "delete"
                            showPopup = true
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreateDestructiveAction,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialSelectedDateMillis
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            key(currentSelectedTime?.hour, currentSelectedTime?.minute) {
                val timePickerState = rememberTimePickerState(
                    initialHour = currentSelectedTime?.hour ?: 12,
                    initialMinute = currentSelectedTime?.minute ?: 0,
                    is24Hour = false
                )

                androidx.compose.ui.window.Dialog(
                    onDismissRequest = { showTimePicker = false }
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = CreateDialogBackground
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Select time",
                                color = CreatePrimaryText,
                                fontWeight = FontWeight.Bold
                            )

                            TimePicker(state = timePickerState)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { showTimePicker = false }) {
                                    Text("Cancel")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        selectedTime = LocalTime.of(
                                            timePickerState.hour,
                                            timePickerState.minute
                                        )
                                        showTimePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showNotificationPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 160.dp)
                        .background(
                            color = CreateDialogBackground,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                        .semantics {
                            contentDescription = "Event notification settings dialog"
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Event Notifications",
                            color = CreatePrimaryText,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (VolunteerStore.areGlobalNotificationsEnabled()) {
                                "Turn notifications for this event on or off."
                            } else {
                                "Global notifications are off in Settings. This event preference will still be saved."
                            },
                            color = CreateSecondaryText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (notificationsEnabled) "On" else "Off",
                                color = CreatePrimaryText
                            )

                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                modifier = Modifier.semantics {
                                    contentDescription = "Notifications for this event"
                                    stateDescription = if (notificationsEnabled) "On" else "Off"
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = CreatePrimaryAction,
                                    uncheckedThumbColor = CreatePrimaryText,
                                    uncheckedTrackColor = CreateBorderColor
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showNotificationPopup = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CreateSecondaryAction,
                                    contentColor = CreatePrimaryText
                                )
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }
        if (showInvalidEmailDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text("Invalid Email")
                },
                text = {
                    Text("Invalid domain / Email could not be verified.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showInvalidEmailDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        if (showInvalidPhoneDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text("Invalid Phone Number")
                },
                text = {
                    Text("Phone number must contain only digits (0–9).")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showInvalidPhoneDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 120.dp)
                        .background(
                            color = CreateDialogBackground,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (popupAction == "delete" && isEditing) {
                                "Delete this event?"
                            } else {
                                "Continue with these changes?"
                            },
                            color = CreatePrimaryText,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (popupAction == "delete" && isEditing) {
                                "This action removes the event."
                            } else {
                                "Review the information before confirming."
                            },
                            color = CreateSecondaryText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    if (popupAction == "submit") {

                                        if (
                                            name.isBlank() ||
                                            email.isBlank() ||
                                            phone.isBlank() ||
                                            eventName.isBlank() ||
                                            role.isBlank()
                                        ) {
                                            formError = "Please fill in all required fields."
                                            showPopup = false
                                            return@Button
                                        }

                                        val finalDate = selectedDate
                                        if (finalDate == null) {

                                            formError = "Please select a date."
                                            showPopup = false
                                            return@Button
                                        }

                                        val finalTime = selectedTime
                                        if (finalTime == null) {
                                            formError = "Please select a time."
                                            showPopup = false
                                            return@Button
                                        }

                                        val volunteer = Volunteer(
                                            name = name,
                                            email = email.trim(),
                                            phone = phone.trim(),
                                            eventName = eventName,
                                            role = role,
                                            notes = notes,
                                            date = finalDate,
                                            time = finalTime,
                                            notificationsEnabled = notificationsEnabled
                                        )

                                        if (isEditing) {
                                            VolunteerStore.update(volunteerIndex, volunteer)
                                        } else {
                                            VolunteerStore.add(volunteer)
                                        }

                                        showPopup = false
                                        activity?.finish()
                                    } else if (popupAction == "delete") {
                                        if (isEditing) {
                                            VolunteerStore.removeAt(volunteerIndex)
                                        }
                                        showPopup = false
                                        activity?.finish()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (popupAction == "delete") CreateDestructiveAction else CreatePrimaryAction,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(
                                    imageVector = if (popupAction == "delete") Icons.Filled.Delete else Icons.Filled.Check,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (popupAction == "delete") "Confirm Delete" else "Confirm")
                            }

                            Button(
                                onClick = { showPopup = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CreateSecondaryAction,
                                    contentColor = CreatePrimaryText
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun accessibleTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CreatePanelBackground,
    unfocusedContainerColor = CreatePanelBackground,
    focusedBorderColor = CreatePrimaryAction,
    unfocusedBorderColor = CreateBorderColor,
    focusedTextColor = CreatePrimaryText,
    unfocusedTextColor = CreatePrimaryText,
    focusedLabelColor = CreatePrimaryAction,
    unfocusedLabelColor = CreateSecondaryText,
    focusedPlaceholderColor = CreateSecondaryText,
    unfocusedPlaceholderColor = CreateSecondaryText,
    focusedSupportingTextColor = CreateSecondaryText,
    unfocusedSupportingTextColor = CreateSecondaryText,
    errorBorderColor = CreateErrorColor,
    errorLabelColor = CreateErrorColor,
    errorTextColor = CreatePrimaryText,
    cursorColor = CreatePrimaryAction
)