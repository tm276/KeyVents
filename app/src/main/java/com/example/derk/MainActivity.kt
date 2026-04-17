package com.example.derk

import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.derk.ui.theme.DerkTheme
import java.time.format.DateTimeFormatter

private val ScreenBackground = Color(0xFF121212)
private val PanelBackground = Color(0xFF1E1E1E)
private val CardBackground = Color(0xFF263238)
private val PrimaryAction = Color(0xFF64B5F6)
private val BorderColor = Color(0xFF90A4AE)
private val PrimaryText = Color(0xFFF5F5F5)
private val SecondaryText = Color(0xFFCFD8DC)
private val EmptyStateText = Color(0xFFB0BEC5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DerkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ScreenBackground
                ) {
                    EventFeedScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        VolunteerStore.version.value = VolunteerStore.version.value + 1
    }
}

@Composable
fun EventFeedScreen() {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    val storeVersion = VolunteerStore.version.value

    val filteredVolunteers: List<IndexedValue<Volunteer>> = remember(searchText, storeVersion) {
        FuzzySearch.filterVolunteers(
            volunteers = VolunteerStore.volunteers.toList(),
            query = searchText,
            maxDistance = 2
        )
    }

    val scrollState = rememberScrollState()
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(12.dp)
    ) {
        Text(
            text = "Event Feed",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 12.dp)
                .semantics { heading() },
            color = PrimaryText,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = PanelBackground,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(12.dp)
                .semantics {
                    contentDescription = "Event feed results"
                    stateDescription =
                        if (filteredVolunteers.isEmpty()) "No events shown" else "${filteredVolunteers.size} events shown"
                }
        ) {
            if (filteredVolunteers.isEmpty()) {
                Text(
                    text = if (searchText.isBlank()) "No events yet" else "No matching results",
                    color = EmptyStateText
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredVolunteers.forEach { indexedVolunteer ->
                        val index = indexedVolunteer.index
                        val volunteer = indexedVolunteer.value

                        val cardAnnouncement = buildString {
                            append("Event ${volunteer.eventName}. ")
                            append("Volunteer ${volunteer.name}. ")
                            append("Role ${volunteer.role}. ")
                            append("Date ${volunteer.date.format(dateFormatter)}. ")
                            append("Time ${volunteer.time.format(timeFormatter)}. ")
                            append("Email ${volunteer.email}. ")
                            append("Phone ${volunteer.phone}. ")
                            if (volunteer.notes.isNotBlank()) {
                                append("Notes ${volunteer.notes}. ")
                            }
                            append("Double tap to edit event.")
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = CardBackground,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .semantics(mergeDescendants = true) {
                                    role = Role.Button
                                    contentDescription = cardAnnouncement
                                }
                                .clickable {
                                    val intent = Intent(context, CreateEventActivity::class.java)
                                    intent.putExtra("volunteerIndex", index)
                                    context.startActivity(intent)
                                }
                                .padding(12.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = volunteer.eventName,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )

                                Text(
                                    text = "Name: ${volunteer.name}",
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Role: ${volunteer.role}",
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Date: ${volunteer.date.format(dateFormatter)}",
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Time: ${volunteer.time.format(timeFormatter)}",
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Email: ${volunteer.email}",
                                    color = SecondaryText
                                )
                                Text(
                                    text = "Phone: ${volunteer.phone}",
                                    color = SecondaryText
                                )

                                if (volunteer.notes.isNotBlank()) {
                                    Text(
                                        text = "Notes: ${volunteer.notes}",
                                        color = SecondaryText
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = "Search events"
                        stateDescription = if (searchText.isBlank()) {
                            "Search field is empty"
                        } else {
                            "Search text entered"
                        }
                    },
                label = { Text("Search events") },
                placeholder = { Text("Search by name, role, date, time, or notes") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PanelBackground,
                    unfocusedContainerColor = PanelBackground,
                    focusedBorderColor = PrimaryAction,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = PrimaryText,
                    unfocusedTextColor = PrimaryText,
                    focusedLabelColor = PrimaryAction,
                    unfocusedLabelColor = SecondaryText,
                    focusedPlaceholderColor = EmptyStateText,
                    unfocusedPlaceholderColor = EmptyStateText,
                    cursorColor = PrimaryAction
                )
            )

            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, CreateEventActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = PrimaryAction,
                contentColor = Color.Black,
                modifier = Modifier.semantics {
                    contentDescription = "Add event"
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        }
    }
}