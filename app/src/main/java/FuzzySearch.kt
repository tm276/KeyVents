package com.example.derk

import java.time.format.DateTimeFormatter
import kotlin.math.max

object FuzzySearch {
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    private val dateRegex = Regex("""\b(\d{1,2})[/-](\d{1,2})([/-](\d{2,4}))?\b""")
    private val timeRegex = Regex("""\b(\d{1,2})(:(\d{2}))?\s?(am|pm)?\b""", RegexOption.IGNORE_CASE)

    fun filterVolunteers(
        volunteers: List<Volunteer>,
        query: String,
        maxDistance: Int = 2
    ): List<IndexedValue<Volunteer>> {
        if (query.isBlank()) {
            return volunteers.withIndex().toList()
        }

        val normalizedQuery = normalize(query)

        return volunteers.withIndex().filter { indexedVolunteer ->
            val volunteer = indexedVolunteer.value

            if (matchesDateQuery(volunteer.date.format(dateFormatter), query)) return@filter true
            if (matchesTimeQuery(volunteer.time.format(timeFormatter), query)) return@filter true

            val searchableFields = listOf(
                volunteer.name,
                volunteer.eventName,
                volunteer.role,
                volunteer.email,
                volunteer.phone,
                volunteer.notes,
                volunteer.date.format(dateFormatter),
                volunteer.time.format(timeFormatter)
            )

            searchableFields.any { field ->
                matchesField(field, normalizedQuery, maxDistance)
            }
        }
    }

    private fun matchesDateQuery(formattedDate: String, query: String): Boolean {
        val match = dateRegex.find(query) ?: return false
        return formattedDate.contains(match.value)
    }

    private fun matchesTimeQuery(formattedTime: String, query: String): Boolean {
        val match = timeRegex.find(query) ?: return false
        val normalized = match.value.trim().lowercase()
        return formattedTime.lowercase().contains(normalized)
    }

    private fun matchesField(field: String, query: String, maxDistance: Int): Boolean {
        val normalizedField = normalize(field)

        if (normalizedField.contains(query)) return true

        val words = normalizedField.split(Regex("\\s+|[-/:,@.]")).filter { it.isNotBlank() }

        if (words.any { word ->
                word.contains(query) ||
                        levenshtein(word, query) <= maxDistance ||
                        (query.length >= 4 &&
                                word.length >= 4 &&
                                levenshtein(word.take(query.length), query) <= maxDistance)
            }) {
            return true
        }

        return slidingWindowMatch(normalizedField, query, maxDistance)
    }

    private fun slidingWindowMatch(text: String, query: String, maxDistance: Int): Boolean {
        if (text.isBlank() || query.isBlank()) return false

        if (text.length < query.length) {
            return levenshtein(text, query) <= maxDistance
        }

        val minWindow = max(1, query.length - maxDistance)
        val maxWindow = minOf(text.length, query.length + maxDistance)

        for (windowSize in minWindow..maxWindow) {
            for (start in 0..text.length - windowSize) {
                val part = text.substring(start, start + windowSize)
                if (levenshtein(part, query) <= maxDistance) {
                    return true
                }
            }
        }

        return false
    }

    private fun normalize(text: String): String {
        return text.trim().lowercase()
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        val dp = Array(a.length + 1) { IntArray(b.length + 1) }

        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[a.length][b.length]
    }
}