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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.derk.ui.theme.DerkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DerkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF10C75A)
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
    LaunchedEffect(storeVersion) { }

    val filteredVolunteers = VolunteerStore.volunteers.withIndex().filter { indexedVolunteer ->
        val volunteer = indexedVolunteer.value
        searchText.isBlank() ||
                volunteer.name.contains(searchText, true) ||
                volunteer.eventName.contains(searchText, true) ||
                volunteer.role.contains(searchText, true) ||
                volunteer.email.contains(searchText, true) ||
                volunteer.phone.contains(searchText, true) ||
                volunteer.notes.contains(searchText, true)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF10C75A))
            .padding(12.dp)
    ) {
        Text(
            text = "Event Feed",
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
            if (filteredVolunteers.isEmpty()) {
                Text(
                    text = "No events yet",
                    color = Color.Black
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

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFE082),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    val intent = Intent(context, CreateEventActivity::class.java)
                                    intent.putExtra("volunteerIndex", index)
                                    context.startActivity(intent)
                                }
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = volunteer.eventName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text("Name: ${volunteer.name}", color = Color.Black)
                                Text("Role: ${volunteer.role}", color = Color.Black)
                                Text("Email: ${volunteer.email}", color = Color.Black)
                                Text("Phone: ${volunteer.phone}", color = Color.Black)

                                if (volunteer.notes.isNotBlank()) {
                                    Text("Notes: ${volunteer.notes}", color = Color.Black)
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
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search...") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2CC4D),
                    unfocusedContainerColor = Color(0xFFF2CC4D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFF2CC4D),
                        shape = CircleShape
                    )
                    .clickable {
                        val intent = Intent(context, CreateEventActivity::class.java)
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Event",
                    tint = Color.Black
                )
            }
        }
    }
}