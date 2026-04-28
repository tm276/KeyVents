package com.example.derk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object EventShareUtils {

    fun openCalendarInsert(context: Context, volunteer: Volunteer) {
        val startMillis = volunteer.date
            .atTime(volunteer.time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endMillis = volunteer.date
            .atTime(volunteer.time.plusHours(1))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val details = buildString {
            append("Volunteer: ${volunteer.name}\n")
            append("Role: ${volunteer.role}\n")
            append("Email: ${volunteer.email}\n")
            append("Phone: ${volunteer.phone}\n")
            if (volunteer.notes.isNotBlank()) {
                append("Notes: ${volunteer.notes}")
            }
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, volunteer.eventName)
            putExtra(CalendarContract.Events.DESCRIPTION, details)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        }

        context.startActivity(intent)
    }

    fun openEmailShare(context: Context, volunteer: Volunteer) {
        val subject = "Event Details: ${volunteer.eventName}"
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val body = buildString {
            append("Event: ${volunteer.eventName}\n")
            append("Name: ${volunteer.name}\n")
            append("Role: ${volunteer.role}\n")
            append("Date: ${volunteer.date.format(dateFormatter)}\n")
            append("Time: ${volunteer.time.format(timeFormatter)}\n")
            append("Email: ${volunteer.email}\n")
            append("Phone: ${volunteer.phone}\n")
            if (volunteer.notes.isNotBlank()) {
                append("Notes: ${volunteer.notes}\n")
            }
            append("\nNotifications: ")
            append(if (volunteer.notificationsEnabled) "On" else "Off")
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        context.startActivity(intent)
    }
}