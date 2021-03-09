package id.islaami.playmi2021.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Created by Kemal Amru Ramadhan on 16/04/2019.
 */
const val ZERO_RUPIAH = "Rp 0"

fun Long?.toCurrencyString(): String {

    val localeId = Locale("in", "ID")

    val currencyFormatter = NumberFormat.getCurrencyInstance(localeId).apply {
        maximumFractionDigits = 0
        isParseIntegerOnly = true
    }

    val formatted = this?.let { currencyFormatter.format(it) } ?: currencyFormatter.format(0)

    val replace = String.format(
        "[Rp\\s]",
        NumberFormat.getCurrencyInstance().currency.getSymbol(localeId)
    )

    return formatted.replace(replace, "").replace("Rp", "Rp ")
}

fun String?.isZeroRupiah(): Boolean = this == ZERO_RUPIAH

fun String?.toCurrencyString(): String = this?.longValue().toCurrencyString()

fun Number?.toCurrencyString(): String = this?.longValue().toCurrencyString()

fun String?.longValue(): Long = this?.toLong()?.let { it } ?: 0

fun Number?.longValue(): Long = this?.toLong()?.let { it } ?: 0

fun String?.fromCurrencyToLong(): Long =
    if (this != null && this.isNotEmpty())
        replace("Rp ", "").replace(".", "").toLong()
    else 0

fun Long?.value(): Long = this ?: 0

fun Int?.value(): Int = this ?: 0

fun Double?.value(): Double = this ?: 0.0