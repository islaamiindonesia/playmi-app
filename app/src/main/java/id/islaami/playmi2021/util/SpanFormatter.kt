package id.islaami.playmi2021.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Provides [String.format] style functions that work with [Spanned] strings and preserve formatting.
 *
 * @author George T. Steel
 * https://github.com/george-steel/android-utils/blob/master/src/org/oshkimaadziig/george/androidutils/SpanFormatter.java
 */
object SpanFormatter {
    val FORMAT_SEQUENCE: Pattern = Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([[a-zA-Z%]&&[^tT]]|[tT][a-zA-Z])")

    /**
     * Version of [String.format] that works on [Spanned] strings to preserve rich text formatting.
     * Both the `format` as well as any `%s args` can be Spanned and will have their formatting preserved.
     * Due to the way [Spannable]s work, any argument's spans will can only be included **once** in the result.
     * Any duplicates will appear as text only.
     *
     * @param format the format string (see [java.util.Formatter.format])
     * @param args
     * the list of arguments passed to the formatter. If there are
     * more arguments than required by `format`,
     * additional arguments are ignored.
     * @return the formatted string (with spans).
     */
    fun format(format: CharSequence?, vararg args: Any?): SpannedString {
        return format(Locale.getDefault(), format, *args)
    }

    /**
     * Version of [String.format] that works on [Spanned] strings to preserve rich text formatting.
     * Both the `format` as well as any `%s args` can be Spanned and will have their formatting preserved.
     * Due to the way [Spannable]s work, any argument's spans will can only be included **once** in the result.
     * Any duplicates will appear as text only.
     *
     * @param locale
     * the locale to apply; `null` value means no localization.
     * @param format the format string (see [java.util.Formatter.format])
     * @param args
     * the list of arguments passed to the formatter.
     * @return the formatted string (with spans).
     * @see String.format
     */
    fun format(locale: Locale?, format: CharSequence?, vararg args: Any?): SpannedString {
        val out = SpannableStringBuilder(format)

        var i = 0
        var argAt = -1

        while (i < out.length) {
            val m: Matcher = FORMAT_SEQUENCE.matcher(out)
            if (!m.find(i)) break
            i = m.start()
            val exprEnd: Int = m.end()

            val argTerm: String = m.group(1)
            val modTerm: String = m.group(2)
            val typeTerm: String = m.group(3)

            var cookedArg: CharSequence

            if (typeTerm == "%") {
                cookedArg = "%"
            } else if (typeTerm == "n") {
                cookedArg = "\n"
            } else {
                var argIdx = 0
                argIdx = if (argTerm == "") ++argAt else if (argTerm == "<") argAt else argTerm.substring(0, argTerm.length - 1).toInt() - 1
                val argItem = args[argIdx]
                if (typeTerm == "s" && argItem is Spanned) {
                    cookedArg = argItem
                } else {
                    cookedArg = java.lang.String.format(locale, "%$modTerm$typeTerm", argItem)
                }
            }
            out.replace(i, exprEnd, cookedArg)
            i += cookedArg.length
        }
        return SpannedString(out)
    }
}