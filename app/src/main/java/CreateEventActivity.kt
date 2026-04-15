package com.example.derk

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.derk.ui.theme.DerkTheme

class CreateEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DerkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF10C75A)
                ) {
                    CreateEventScreen()
                }
            }
        }
    }
}

@Composable
fun CreateEventScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    var showPopup by remember { mutableStateOf(false) }
    var popupAction by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

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
                text = "Create Event",
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
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .clickable {
                                    showPopup = false

                                    if (popupAction == "submit") {
                                        VolunteerStore.add(
                                            Volunteer(
                                                name = name,
                                                email = email,
                                                phone = phone,
                                                eventName = eventName,
                                                role = role,
                                                notes = notes
                                            )
                                        )
                                        activity?.finish()
                                    } else if (popupAction == "delete") {
                                        name = ""
                                        email = ""
                                        phone = ""
                                        eventName = ""
                                        role = ""
                                        notes = ""
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