@file:Suppress("MoveLambdaOutsideParentheses")

package id.islaami.playmi.util.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import id.islaami.playmi.util.fromCurrencyToLong
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.*

/**
 * Created by Kemal Amru Ramadhan on 10/04/2019.
 */

fun TextInputLayout.validate(
    errorMessage: String,
    validator: (String) -> Boolean = { s -> s.isNotEmpty() }
): Boolean =
    this.editText?.validate(this, errorMessage, validator) == true

fun EditText.validate(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    validator: (String) -> Boolean = { s -> s.isNotEmpty() }
): Boolean {
    return if (validator(getString())) {
        textInputLayout.hideError()
        true
    } else {
        textInputLayout.showError(errorMessage)
        false
    }
}

fun EditText.setTextPostalCodeListener(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    afterTextChangedHandler: ((Long) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(input: Editable?) {
            this@setTextPostalCodeListener.validate(
                textInputLayout,
                errorMessage,
                { c -> c.isNotEmpty() && c.length == 5 }
            )

            afterTextChangedHandler?.run { this }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.setTextChangedCurrencyListener(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    afterTextChangedHandler: ((Long) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        var current = ""

        override fun afterTextChanged(input: Editable?) {
            this@setTextChangedCurrencyListener.validate(
                textInputLayout,
                errorMessage,
                { c -> c.isNotEmpty() && c != "Rp" && c != "Rp 0" && c != "0" }
            )

            if (input.toString() != current) {
                this@setTextChangedCurrencyListener.removeTextChangedListener(this)

                val localeId = Locale("in", "ID")
                val cleanString = input.toString().replace("Rp", "").replace(".", "")

                val parsed = try {
                    cleanString.toDouble()
                } catch (e: NumberFormatException) {
                    0.00
                }

                val currencyFormatter = NumberFormat.getCurrencyInstance(localeId).apply {
                    maximumFractionDigits = 0
                    isParseIntegerOnly = true
                }
                val formatted = currencyFormatter.format(parsed)
                current = formatted

                val replace = String.format(
                    "[Rp\\s]",
                    NumberFormat.getCurrencyInstance().currency.getSymbol(localeId)
                )
                val result = formatted.replace(replace, "").replace("Rp", "Rp ")

                this@setTextChangedCurrencyListener.setText(result)
                this@setTextChangedCurrencyListener.setSelection(result.length)

                afterTextChangedHandler?.run { this(result.fromCurrencyToLong()) }

                this@setTextChangedCurrencyListener.addTextChangedListener(this)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.setTextChangedYearListener(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    afterTextChangedHandler: ((String) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(input: Editable?) {
            this@setTextChangedYearListener.validate(
                textInputLayout,
                errorMessage,
                { c -> c.isNotEmpty() && c != "0000" }
            )

            afterTextChangedHandler?.run { this }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

fun EditText.setTextChangedIncomeListener(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    afterTextChangedHandler: ((Long) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        var current = ""

        override fun afterTextChanged(input: Editable?) {
            this@setTextChangedIncomeListener.validate(
                textInputLayout,
                errorMessage,
                { c -> c.isNotEmpty() }
            )

            if (input.toString() != current) {
                this@setTextChangedIncomeListener.removeTextChangedListener(this)

                val localeId = Locale("in", "ID")
                val cleanString = input.toString().replace("Rp", "").replace(".", "")

                val parsed = try {
                    cleanString.toDouble()
                } catch (e: NumberFormatException) {
                    0.00
                }

                val currencyFormatter = NumberFormat.getCurrencyInstance(localeId).apply {
                    maximumFractionDigits = 0
                    isParseIntegerOnly = true
                }
                val formatted = currencyFormatter.format(parsed)
                current = formatted

                val replace = String.format(
                    "[Rp\\s]",
                    NumberFormat.getCurrencyInstance().currency.getSymbol(localeId)
                )
                val result = formatted.replace(replace, "").replace("Rp", "Rp ")

                this@setTextChangedIncomeListener.setText(result)
                this@setTextChangedIncomeListener.setSelection(result.length)

                afterTextChangedHandler?.run { this(result.fromCurrencyToLong()) }

                this@setTextChangedIncomeListener.addTextChangedListener(this)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}


fun EditText.setTextChangedListener(
    textInputLayout: TextInputLayout,
    errorMessage: String,
    afterTextChangedHandler: ((String) -> Unit)? = null
) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            this@setTextChangedListener.validate(
                textInputLayout,
                errorMessage,
                { c -> c.isNotEmpty() }
            )

            afterTextChangedHandler?.run { this(s.toString()) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

fun EditText.getString(): String = text.toString()

fun TextInputLayout.showError(errorMessage: String) {
    isErrorEnabled = true
    error = errorMessage
    requestFocus()
}

fun TextInputLayout.hideError() {
    isErrorEnabled = false
}
