package com.example.playmi.util

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.playmi.R
import com.example.playmi.data.model.ErrorResponse
import com.example.playmi.ui.auth.LoginActivity
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.util.*

fun Throwable.getErrorMessage(): String {
    if (this is HttpException) {
        val errorBody = response()?.errorBody()?.string()
        return when (response()?.code()) {
            in 500 until 599 -> processErrorMessage(ERROR_INTERNAL_SERVER, errorBody.toString())
            401 -> processErrorMessage(ERROR_UNAUTHORIZED, errorBody.toString())
            /*403 -> processErrorMessage(ERROR_INVALID_TOKEN, errorBody.toString())
            404 -> processErrorMessage(ERROR_PAGE_NOT_FOUND, errorBody.toString())
            405 -> processErrorMessage(ERROR_METHOD_NOT_ALLOWED, errorBody.toString())*/
            else -> processErrorMessage(ERROR_CONNECTION, errorBody.toString())
        }
    }
    return ERROR_DEFAULT
}

fun processErrorMessage(default: String, json: String): String {
    return try {
        val jsonErrorAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
        val error = jsonErrorAdapter.fromJson(json)
        error?.message.toString()
    } catch (ioException: IOException) {
        default
    }
}

fun String?.isNotCommonApiError(): Boolean {
    /*return (this != null &&
            this != ERROR_INTERNAL_SERVER &&
            this != ERROR_INVALID_TOKEN &&
            this != ERROR_PAGE_NOT_FOUND &&
            this != ERROR_METHOD_NOT_ALLOWED &&
            this != ERROR_UNAUTHORIZED &&
            this != ERROR_CONNECTION)*/
    return this != ERROR_UNAUTHORIZED
}

fun Context.checkForCommonApiError(errorMessage: String?) {
    if (errorMessage != null) {
        when (errorMessage) {
            ERROR_UNAUTHORIZED -> {
                LoginActivity.startActivityWhenErrorInvalidToken(this)
            }
            /*ERROR_INVALID_TOKEN, ERROR_UNAUTHORIZED_SUSPENDED -> {
                LoginActivity.startActivityWhenErrorSuspended(this)
            }*/
        }
    }
}

fun Context.checkApiForMessage(errorMessage: String?): String =
    when (errorMessage.toString().toLowerCase(Locale("id", "ID"))) {
        ERROR_UNVERIFIED_ACCOUNT -> getString(R.string.error_email_not_verified)
        else -> getString(R.string.error_message_default)
    }

/**
 * Handle error without showing error layout
 *
 * processErrorMessage: What to do when error happen
 */
fun Activity.handleApiError(
    errorMessage: String?,
    processErrorMessage: ((String) -> Unit)? = null
) {
    checkForCommonApiError(errorMessage)
    if (processErrorMessage != null) {
        if (errorMessage != null && errorMessage.isNotCommonApiError()) {
            processErrorMessage(errorMessage)
        } else {
            val message = checkApiForMessage(errorMessage)
            processErrorMessage(message)
        }
    }
}

fun Fragment.handleApiError(errorMessage: String?, errorHandler: ((String) -> Unit)? = null) {
    activity?.handleApiError(errorMessage, errorHandler)
}

/**
 * Handle error by showing error api page (R.layout.error_api_page)
 *//*

fun Activity.handleApiErrorAndShowingErrorApiPage(
    errorMessage: String?,
    errorLayout: ViewGroup,
    successLayout: ViewGroup,
    retryAction: () -> Unit
) {
    checkForCommonApiError(errorMessage)
    val message =
        if (errorMessage != null && errorMessage.isNotCommonApiError()) errorMessage
        else checkApiForMessage(errorMessage)

    showErrorApiPage(
        message,
        errorLayout,
        successLayout,
        retryAction
    )
}

fun Fragment.handleApiErrorAndShowingErrorApiPage(
    errorMessage: String?,
    errorLayout: ViewGroup,
    successLayout: ViewGroup,
    retryAction: () -> Unit
) {
    activity?.handleApiErrorAndShowingErrorApiPage(
        errorMessage,
        errorLayout,
        successLayout,
        retryAction
    )
}

fun Activity.handleApiErrorAndShowingErrorDialog(
    fragmentManager: FragmentManager,
    dialogType: String = DIALOG_DEFAULT,
    errorMessage: String?,
    primaryText: String? = null,
    secondaryText: String? = null,
    primaryAction: (() -> Unit)? = null,
    secondaryAction: (() -> Unit)? = null
) {
    checkForCommonApiError(errorMessage)
    val message = checkApiForMessage(errorMessage)

    when (dialogType) {
        DIALOG_TWO_BUTTONS -> showErrorApiDialogTwoButton(
            fragmentManager = fragmentManager,
            text = message,
            primaryText = primaryText,
            secondaryText = secondaryText,
            primaryAction = primaryAction,
            secondaryAction = secondaryAction
        )
        DIALOG_IMAGE_BUTTON -> showErrorApiDialogImageButton(
            fragmentManager = fragmentManager,
            text = message,
            okCallback = primaryAction,
            outsideTouchCallback = secondaryAction
        )
        else -> showErrorApiDialog(
            fragmentManager = fragmentManager,
            text = message,
            btnText = primaryText,
            okCallback = primaryAction,
            outsideTouchCallback = secondaryAction
        )
    }
}

fun Fragment.handleApiErrorAndShowingErrorDialog(
    fragmentManager: FragmentManager,
    dialogType: String = DIALOG_DEFAULT,
    errorMessage: String?,
    primaryAction: (() -> Unit)? = null,
    secondaryAction: (() -> Unit)? = null
) {
    activity?.handleApiErrorAndShowingErrorDialog(
        fragmentManager = fragmentManager,
        dialogType = dialogType,
        errorMessage = errorMessage,
        primaryAction = primaryAction,
        secondaryAction = secondaryAction
    )
}

fun showErrorApiPage(
    errorMessage: String,
    errorLayout: ViewGroup,
    successLayout: ViewGroup,
    retryAction: () -> Unit
) {
    val errorPage = errorLayout.inflate(R.layout.error_api_page, false)
    with(errorPage) {
        btnRetry.setOnClickListener { retryAction() }
        tvError.text = errorMessage
    }

    errorLayout.removeAllViews()
    errorLayout.addView(errorPage)

    errorLayout.setVisibilityToVisible()
    successLayout.setVisibilityToGone()
}

fun showErrorEmptyPage(errorLayout: ViewGroup, successLayout: ViewGroup) {
    val emptyPage = errorLayout.inflate(R.layout.error_empty_page, false)

    errorLayout.removeAllViews()
    errorLayout.addView(emptyPage)

    errorLayout.setVisibilityToVisible()
    successLayout.setVisibilityToGone()
}

fun hideErrorLayout(errorLayout: ViewGroup, successLayout: ViewGroup) {
    errorLayout.setVisibilityToGone()
    successLayout.setVisibilityToVisible()
}

fun showErrorApiDialog(
    fragmentManager: FragmentManager,
    text: String,
    imageRes: Int? = null,
    btnText: String? = null,
    okCallback: (() -> Unit)?,
    outsideTouchCallback: (() -> Unit)?
) {
    CustomDialogFragment.show(
        fragmentManager = fragmentManager,
        text = text,
        image = imageRes,
        btnText = btnText,
        okCallback = { okCallback?.invoke() },
        outsideTouchCallback = { outsideTouchCallback?.invoke() }
    )
}

fun showErrorApiDialogTwoButton(
    fragmentManager: FragmentManager,
    text: String,
    primaryText: String? = null,
    secondaryText: String? = null,
    primaryAction: (() -> Unit)? = null,
    secondaryAction: (() -> Unit)? = null
) {
    CustomDialogTwoButtonFragment.show(
        fragmentManager = fragmentManager,
        text = text,
        btnPrimaryText = primaryText,
        btnSecondaryText = secondaryText,
        primaryCallback = { primaryAction?.invoke() },
        secondaryCallback = { secondaryAction?.invoke() },
        outsideTouchCallback = { secondaryAction?.invoke() }
    )
}

fun showErrorApiDialogImageButton(
    fragmentManager: FragmentManager,
    text: String,
    okCallback: (() -> Unit)? = null,
    outsideTouchCallback: (() -> Unit)? = null
) {
    CustomDialogImageButtonFragment.show(
        fragmentManager = fragmentManager,
        text = text,
        okCallback = { okCallback?.invoke() },
        outsideTouchCallback = { outsideTouchCallback?.invoke() }
    )
}*/
