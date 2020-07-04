package id.islaami.playmi.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import id.islaami.playmi.R
import id.islaami.playmi.data.model.ErrorResponse
import id.islaami.playmi.ui.auth.LoginActivity
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import kotlin.math.log

fun Throwable.getErrorMessage(): String {
    when (this) {
        is HttpException -> {
            val errorBody = response()?.errorBody()?.string()
            return when (response()?.code()) {
                in 500 until 599 -> processErrorMessage(ERROR_INTERNAL_SERVER, errorBody.toString())
                401 -> processErrorMessage(ERROR_UNAUTHORIZED, errorBody.toString())
                403 -> processErrorMessage(ERROR_INVALID_TOKEN, errorBody.toString())
                404 -> processErrorMessage(ERROR_PAGE_NOT_FOUND, errorBody.toString())
                405 -> processErrorMessage(ERROR_METHOD_NOT_ALLOWED, errorBody.toString())
                else -> processErrorMessage(ERROR_DEFAULT, errorBody.toString())
            }
        }
        is SocketTimeoutException, is ConnectException -> {
            return ERROR_CONNECTION_TIMEOUT
        }
        is UnknownHostException -> {
            return ERROR_CONNECTION
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

fun Context.checkForCommonApiError(errorMessage: String?) {
    if (errorMessage != null) {
        when (errorMessage) {
            ERROR_INVALID_TOKEN-> {
                LoginActivity.startActivityWhenErrorInvalidToken(this)
            }
        }
    }
}

fun Context.checkApiForMessage(errorMessage: String?): String =
    when (errorMessage.toString()) {
        ERROR_CONNECTION_TIMEOUT -> getString(R.string.error_connection_timeout)
        ERROR_CONNECTION -> getString(R.string.error_connection)
        else -> getString(R.string.error_message_default)
    }

/**
 * Handle error without showing error layout
 *
 * processErrorMessage: What to do when error happen
 */
fun Activity.handleApiError(
    errorMessage: String?,
    processErrorMessage: ((String) -> Unit)
) {
    checkForCommonApiError(errorMessage)
    processErrorMessage.invoke(checkApiForMessage(errorMessage))
}

fun Fragment.handleApiError(errorMessage: String?, errorHandler: ((String) -> Unit)) {
    activity?.handleApiError(errorMessage, errorHandler)
}
