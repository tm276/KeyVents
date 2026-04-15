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

    fun update(index: Int, volunteer: Volunteer) {
        if (index in volunteers.indices) {
            volunteers[index] = volunteer
            version.value = version.value + 1
        }
    }

    fun removeAt(index: Int) {
        if (index in volunteers.indices) {
            volunteers.removeAt(index)
            version.value = version.value + 1
        }
    }

    fun get(index: Int): Volunteer? {
        return if (index in volunteers.indices) volunteers[index] else null
    }
}