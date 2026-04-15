package com.example.derk

import androidx.compose.runtime.mutableIntStateOf

data class Volunteer(
    val name: String,
    val email: String,
    val phone: String,
    val eventName: String,
    val role: String,
    val notes: String
)

object VolunteerStore {
    val volunteers = mutableListOf<Volunteer>()

    var version = mutableIntStateOf(0)

    fun add(volunteer: Volunteer) {
        volunteers.add(volunteer)
        version.value = version.value + 1
    }

    fun remove(volunteer: Volunteer) {
        volunteers.remove(volunteer)
        version.value = version.value + 1
    }

    fun clear() {
        volunteers.clear()
        version.value = version.value + 1
    }
}