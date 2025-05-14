
package com.example.app.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun formatCurrency(amount: Double): String {
    val formatter = DecimalFormat("#,###", DecimalFormatSymbols().apply {
        groupingSeparator = '.'
    })
    return "${formatter.format(amount)} VND"
}
