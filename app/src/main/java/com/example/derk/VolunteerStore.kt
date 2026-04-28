package com.example.derk

import androidx.compose.runtime.mutableIntStateOf
import java.time.LocalDate
import java.time.LocalTime

data class Volunteer(
    val name: String,
    val email: String,
    val phone: String,
    val eventName: String,
    val role: String,
    val notes: String,
    val date: LocalDate,
    val time: LocalTime,
    val notificationsEnabled: Boolean = true
)

object VolunteerStore {
    val volunteers = mutableListOf<Volunteer>()
    var version = mutableIntStateOf(0)

    private var globalNotificationsState = mutableIntStateOf(1)

    fun areGlobalNotificationsEnabled(): Boolean {
        return globalNotificationsState.intValue == 1
    }

    fun setGlobalNotificationsEnabled(enabled: Boolean) {
        globalNotificationsState.intValue = if (enabled) 1 else 0
        version.value = version.value + 1
    }

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

    fun setEventNotificationsEnabled(index: Int, enabled: Boolean) {
        if (index in volunteers.indices) {
            val current = volunteers[index]
            volunteers[index] = current.copy(notificationsEnabled = enabled)
            version.value = version.value + 1
        }
    }

    fun areNotificationsActiveFor(index: Int): Boolean {
        val volunteer = get(index) ?: return false
        return areGlobalNotificationsEnabled() && volunteer.notificationsEnabled
    }
}