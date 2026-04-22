package com.example.derk

import java.time.format.DateTimeFormatter

object FuzzySearch {
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun filterVolunteers(
        volunteers: List<Volunteer>,
        query: String,
        maxDistance: Int = 2
    ): List<IndexedValue<Volunteer>> {
        if (query.isBlank()) {
            return volunteers.withIndex().toList()
        }

        val normalizedQuery = query.trim().lowercase()

        return volunteers.withIndex().filter { indexedVolunteer ->
            val volunteer = indexedVolunteer.value
            val fields = listOf(
                volunteer.name,
                volunteer.eventName,
                volunteer.role,
                volunteer.email,
                volunteer.phone,
                volunteer.notes,
                volunteer.date.format(dateFormatter),
                volunteer.time.format(timeFormatter)
            )

            fields.any { field ->
                val normalizedField = field.lowercase()
                if (normalizedField.contains(normalizedQuery)) {
                    true
                } else {
                    fuzzyMatch(normalizedField, normalizedQuery, maxDistance)
                }
            }
        }
    }

    private fun fuzzyMatch(text: String, query: String, maxDistance: Int): Boolean {
        if (query.length > text.length + maxDistance) return false
        
        // Simple fuzzy match: check if query can be found in text with at most maxDistance edits
        // For performance and simplicity, we'll check if any substring of text has a small Levenshtein distance to query
        // or just use a basic Levenshtein if query is short.
        
        // A more common "fuzzy search" in UI is actually just checking if all characters of query 
        // appear in text in order.
        
        // But the signature has maxDistance, so let's implement Levenshtein distance for substrings.
        
        if (text.length < query.length) {
            return levenshteinDistance(text, query) <= maxDistance
        }

        // Check if any substring of text of similar length to query matches
        for (i in 0..text.length - query.length) {
            val sub = text.substring(i, (i + query.length).coerceAtMost(text.length))
            if (levenshteinDistance(sub, query) <= maxDistance) return true
        }
        
        return false
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
