package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatHelper {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun formatCurrency(amount: Double, symbol: String = "₹"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return "$symbol ${formatter.format(amount)}"
    }

    fun formatDate(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    fun formatShortDate(millis: Long): String {
        return shortDateFormat.format(Date(millis))
    }
}
