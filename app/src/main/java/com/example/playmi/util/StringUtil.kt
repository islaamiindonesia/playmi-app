package com.example.playmi.util

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Patterns
import androidx.core.text.HtmlCompat
import java.text.NumberFormat
import java.util.*

/**
 * Created by Kemal Amru Ramadhan on 10/04/2019.
 */
fun String?.isValidEmail(): Boolean =
    this != null && isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String?.toDouble(): Double {
    if (this != null && this.isNotEmpty()) return this.toDouble()
    return 0.0
}

fun String?.digitGrouping(): String {
    if (this != null && this.isNotEmpty())
        return NumberFormat.getInstance(Locale.ITALY).format(this.toDouble().toInt())
    return "0"
}

fun String?.fromHtmlToString() = this?.let {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString()
    else Html.fromHtml(it).toString()
} ?: ""

fun String?.fromHtmlToSpanned() : Spanned? = this?.let {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
    else HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

fun String?.containsAlphabetAndNumber(): Boolean =
    this?.matches(Regex("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)\$")) == true
