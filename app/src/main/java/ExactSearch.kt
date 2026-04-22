package com.example.derk

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object ExactSearch {
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US)
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    fun filterVolunteers(
        volunteers: List<Volunteer>,
        query: String
    ): List<IndexedValue<Volunteer>> {
        if (query.isBlank()) {
            return volunteers.withIndex().toList()
        }

        val normalizedQuery = normalize(query)
        val normalizedDateQuery = normalizeDateQuery(query)
        val normalizedTimeQuery = normalizeTimeQuery(query)

        val exactMatches = mutableListOf<IndexedValue<Volunteer>>()
        val prefixMatches = mutableListOf<IndexedValue<Volunteer>>()

        for (indexedVolunteer in volunteers.withIndex()) {
            val volunteer = indexedVolunteer.value

            val textFields = listOf(
                volunteer.name,
                volunteer.eventName,
                volunteer.role,
                volunteer.email,
                volunteer.phone,
                volunteer.notes
            )

            val normalizedFields = textFields.map(::normalize)

            val exactTextMatch = normalizedFields.any { it == normalizedQuery }
            val prefixTextMatch = normalizedFields.any { it.startsWith(normalizedQuery) }

            val formattedDate = volunteer.date.format(dateFormatter)
            val formattedTime = volunteer.time.format(timeFormatter)

            val exactDateMatch =
                normalizedDateQuery != null && formattedDate == normalizedDateQuery

            val prefixDateMatch =
                normalizedDateQuery != null && formattedDate.startsWith(normalizedDateQuery)

            val exactTimeMatch =
                normalizedTimeQuery != null && formattedTime == normalizedTimeQuery

            val prefixTimeMatch =
                normalizedTimeQuery != null && formattedTime.startsWith(normalizedTimeQuery)

            val isExactMatch = exactTextMatch || exactDateMatch || exactTimeMatch
            val isPrefixMatch = prefixTextMatch || prefixDateMatch || prefixTimeMatch

            when {
                isExactMatch -> exactMatches.add(indexedVolunteer)
                isPrefixMatch -> prefixMatches.add(indexedVolunteer)
            }
        }

        return exactMatches + prefixMatches
    }

    private fun normalize(text: String): String {
        return text.trim().lowercase(Locale.US)
    }

    private fun normalizeDateQuery(query: String): String? {
        val trimmed = query.trim()

        val patterns = listOf(
            "M/d/yyyy",
            "M-d-yyyy",
            "MM/dd/yyyy",
            "MM-dd-yyyy",
            "M/d/yy",
            "M-d-yy",
            "MM/dd/yy",
            "MM-dd-yy"
        )

        for (pattern in patterns) {
            try {
                val parsedDate = LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(pattern, Locale.US))
                return parsedDate.format(dateFormatter)
            } catch (_: DateTimeParseException) {
            }
        }

        return null
    }

    private fun normalizeTimeQuery(query: String): String? {
        val trimmed = query.trim().uppercase(Locale.US)

        val patterns = listOf(
            "h:mm a",
            "hh:mm a",
            "h:mma",
            "hh:mma",
            "h a",
            "ha",
            "H:mm",
            "HH:mm",
            "H"
        )

        for (pattern in patterns) {
            try {
                val parsedTime = LocalTime.parse(trimmed, DateTimeFormatter.ofPattern(pattern, Locale.US))
                return parsedTime.format(timeFormatter)
            } catch (_: DateTimeParseException) {
            }
        }

        return null
    }
}