package com.example.derk

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.derk.ui.theme.DerkTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CreateEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val volunteerIndex = intent.getIntExtra("volunteerIndex", -1)

        setContent {
            DerkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF10C75A)
                ) {
                    CreateEventScreen(volunteerIndex = volunteerIndex)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(volunteerIndex: Int = -1) {
    val context = LocalContext.current
    val activity = context as? Activity

    val existingVolunteer = if (volunteerIndex >= 0) VolunteerStore.get(volunteerIndex) else null
    val isEditing = existingVolunteer != null

    var showPopup by remember { mutableStateOf(false) }
    var popupAction by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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

            initialSelectedDateMillis = existingVolunteer.date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    val dateDisplay = selectedDate?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) ?: "Select Date"
    val timeDisplay = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select Time"

    val currentSelectedTime by rememberUpdatedState(selectedTime)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10C75A))
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
                    .padding(vertical = 12.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFFF2CC4D),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            formError = ""
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            formError = ""
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            formError = ""
                        },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = eventName,
                        onValueChange = {
                            eventName = it
                            formError = ""
                        },
                        label = { Text("Event Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = {
                            role = it
                            formError = ""
                        },
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                            formError = ""
                        },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            formError = ""
                            showDatePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFE082),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(dateDisplay)
                    }

                    Button(
                        onClick = {
                            formError = ""
                            showTimePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFE082),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(timeDisplay)
                    }

                    if (formError.isNotBlank()) {
                        Text(
                            text = formError,
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold
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
                        containerColor = Color(0xFFF2CC4D),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Submit")
                }

                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFE53935), CircleShape)
                            .clickable {
                                popupAction = "delete"
                                showPopup = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗑", color = Color.White)
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
                        color = Color(0xFFEDEDED)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TimePicker(state = timePickerState)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { showTimePicker = false }) {
                                    Text("Cancel")
                                }

                                Spacer(modifier = Modifier.size(8.dp))

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

        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 120.dp)
                        .background(
                            color = Color(0xFFEDEDED),
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
                                "Do you wish to delete this item"
                            } else {
                                "Do you wish to continue with changes"
                            },
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                                    .clickable {
                                        if (popupAction == "submit") {
                                            if (name.isBlank() ||
                                                email.isBlank() ||
                                                phone.isBlank() ||
                                                eventName.isBlank() ||
                                                role.isBlank()
                                            ) {
                                                formError = "Please fill in all required fields."
                                                showPopup = false
                                                return@clickable
                                            }

                                            val finalDate = selectedDate
                                            if (finalDate == null) {
                                                formError = "Please select a date."
                                                showPopup = false
                                                return@clickable
                                            }

                                            val finalTime = selectedTime
                                            if (finalTime == null) {
                                                formError = "Please select a time."
                                                showPopup = false
                                                return@clickable
                                            }

                                            val volunteer = Volunteer(
                                                name = name,
                                                email = email,
                                                phone = phone,
                                                eventName = eventName,
                                                role = role,
                                                notes = notes,
                                                date = finalDate,
                                                time = finalTime
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
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✔", color = Color.White, fontSize = 28.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFE53935), CircleShape)
                                    .clickable {
                                        showPopup = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✖", color = Color.White, fontSize = 28.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}