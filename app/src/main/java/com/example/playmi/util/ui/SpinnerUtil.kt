package com.example.playmi.util.ui

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.playmi.R
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToGone
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToInvisible
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToVisible

/**
 * Created by Kemal Amru Ramadhan on 10/04/2019.
 */
fun Spinner.validate(
    tvError: TextView?,
    validator: (Int) -> Boolean = { position -> position > 0 }
): Boolean {
    return if (validator(selectedItemPosition)) {
        tvError?.setVisibilityToGone()
        true
    } else {
        tvError?.setVisibilityToVisible()
        tvError?.requestFocus()
        false
    }
}

fun Spinner.validateWithoutShowingError(
    tvError: TextView?,
    validator: (Int) -> Boolean = { position -> position > 0 }
): Boolean {
    return if (validator(selectedItemPosition)) {
        tvError?.setVisibilityToGone()
        true
    } else {
        false
    }
}

/**
 * Show label of spinner when user make selection
 */
fun Spinner.showLabel(
    tvLabel: TextView?,
    validator: (Int) -> Boolean = { position -> position > 0 }
) {
    if (validator(selectedItemPosition)) {
        tvLabel?.setVisibilityToVisible()
    } else {
        tvLabel?.setVisibilityToInvisible()
    }
}

fun Spinner.setItemSelectedListener(
    tvLabel: TextView?,
    tvError: TextView?,
    validator: (Int) -> Boolean = { position -> position >= 0 },
    isSpinnerSelected: Boolean = false,
    validateShowError: Boolean = true,
    selectedItemHandler: ((Int) -> Unit)? = null
) {
    onItemSelectedListener = null
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            showLabel(tvLabel, validator)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            showLabel(tvLabel, validator)
            selectedItemHandler?.run { this(position) }

            if (isSpinnerSelected) {
                if (validateShowError) validate(tvError, validator)
                else validateWithoutShowingError(tvError, validator)
            } else {
                setItemSelectedListener(tvLabel, tvError, validator, true, validateShowError, selectedItemHandler)
            }
        }
    }
}

fun <T> Spinner.setAdapterUsingList(list: List<T>?) {
    if (list != null) {
        this.adapter = ArrayAdapter(
            this.context,
            R.layout.support_simple_spinner_dropdown_item,
            list
        )
    }
}

fun <T> Spinner.setAdapterUsingArray(array: Array<T>) {
    this.adapter = ArrayAdapter(
        this.context,
        R.layout.support_simple_spinner_dropdown_item,
        array
    )
}