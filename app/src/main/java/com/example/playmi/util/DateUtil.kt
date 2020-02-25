package com.example.playmi.util

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kemal Amru Ramadhan on 25/07/2019.
 */

private val localeIndonesia: Locale = Locale("id", "ID")

fun String?.fromAppsFormatDateToDbFormatDate(): String? = this?.let { input ->
    val parser = SimpleDateFormat("dd MMMM yyyy", localeIndonesia)
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    try {
        formatter.format(parser.parse(input))
    } catch (parseException: ParseException) {
        Log.d("HEIKAMU", parseException.message.toString())
        null
    }
}

fun String?.fromDbFormatDateToCustomFormatDate(outputDateFormat: String): String? = this?.let { input ->
    val parser = SimpleDateFormat("yyyy-MM-dd")
    val formatter = SimpleDateFormat(outputDateFormat, localeIndonesia)

    try {
        formatter.format(parser.parse(input))
    } catch (parseException: ParseException) {
        null
    }
}

fun String?.fromDbFormatDateToCalendarFormat(): Date? = this?.let { input ->
    val parser = SimpleDateFormat("yyyy-MM-dd")

    try {
        parser.parse(input)
    } catch (parseException: ParseException) {
        null
    }
}

fun String?.fromDbFormatDateToAppsFormatDate(): String? = this?.let { input ->
    val parser = SimpleDateFormat("yyyy-MM-dd")
    val formatter = SimpleDateFormat("dd MMMM yyyy", localeIndonesia)

    try {
        formatter.format(parser.parse(input))
    } catch (parseException: ParseException) {
        null
    }
}

fun String?.fromDbFormatDateTimeToAppsFormatDate(): String? = this?.let { input ->
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val formatter = SimpleDateFormat("dd MMMM yyyy", localeIndonesia)

    try {
        formatter.format(parser.parse(input))
    } catch (parseException: ParseException) {
        null
    }
}

fun String?.fromDbFormatDateTimeToAppsFormatTime(): String? = this?.let { input ->
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val formatter = SimpleDateFormat("HH:mm 'WIB'", localeIndonesia)

    try {
        formatter.format(parser.parse(input))
    } catch (parseException: ParseException) {
        null
    }
}

fun String?.fromDbFormatDateTimeToAppsFormatDateTime(): String? = this?.let { input ->
    val splitDateTime = input.split(".")
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("dd MMMM yyyy '•' HH:mm 'WIB'", localeIndonesia)

    try {
        formatter.format(parser.parse(splitDateTime[0]))
    } catch (parseException: ParseException) {
        null
    }
}
