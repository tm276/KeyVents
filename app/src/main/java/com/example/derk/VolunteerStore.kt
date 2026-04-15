package com.example.derk

// Data structure
data class Volunteer(
    val name: String,
    val email: String,
    val phone: String,
    val eventName: String,
    val role: String,
    val notes: String
)

// Simple in-memory storage (vector-like)
object VolunteerStore {

    val volunteers = mutableListOf<Volunteer>()

    fun add(volunteer: Volunteer) {
        volunteers.add(volunteer)
    }

    fun remove(volunteer: Volunteer) {
        volunteers.remove(volunteer)
    }

    fun clear() {
        volunteers.clear()
    }
}