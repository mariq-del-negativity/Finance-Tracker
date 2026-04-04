package com.example.keuangankita.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(amount)}"
    }

    fun formatSigned(amount: Double, jenis: String): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return if (jenis == "pemasukan") {
            "+ Rp ${formatter.format(amount)}"
        } else {
            "- Rp ${formatter.format(amount)}"
        }
    }
}
