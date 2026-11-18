package com.jetpack.moviecataloguejetpack.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "-"
        return try {
            val date = inputFormat.parse(dateString)
            if (date != null) outputFormat.format(date) else dateString
        } catch (e: ParseException) {
            dateString
        }
    }

    fun formatRuntime(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "-"
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${remainingMinutes}m"
        }
    }

    fun formatYear(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return if (dateString.length >= 4) {
            "(${dateString.substring(0, 4)})"
        } else {
            ""
        }
    }
}
