package id.islaami.playmi.util.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import id.islaami.playmi.R
import id.islaami.playmi.util.STORAGE_URL

fun View.setVisibilityToVisible() {
    visibility = View.VISIBLE
}

fun View.setVisibilityToInvisible() {
    visibility = View.INVISIBLE
}

fun View.setVisibilityToGone() {
    visibility = View.GONE
}

fun SwipeRefreshLayout.startRefreshing() {
    isRefreshing = true
}

fun SwipeRefreshLayout.stopRefreshing() {
    isRefreshing = false
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ImageView.loadImage(url: String?) = Glide.with(this).load("$STORAGE_URL$url").into(this)

fun ImageView.loadExternalImage(url: String?) = Glide.with(this).load(url).into(this)

fun ImageView.loadImage(imageAsset: Int) = Glide.with(this).load(imageAsset).into(this)

fun ImageView.loadImage(bitmap: Bitmap?) = Glide.with(this).load(bitmap).into(this)

fun ImageView.loadImageCircle(url: String) =
    Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(this)

fun TextView.setIcon(drawable: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0)
}

fun Activity.showSnackbar(message: String?, duration: Int? = null) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: "",
        Snackbar.LENGTH_SHORT
    ).apply {
        duration?.let { this.duration = it }
        show()
    }
}

fun Fragment.showSnackbar(message: String?, duration: Int? = null) {
    activity?.showSnackbar(message, duration)
}

fun Activity.showSnackbarWithUndo(message: String?, duration: Int? = null, undoAction: () -> Unit) {
    Snackbar.make(
        findViewById(android.R.id.content),
        message ?: "",
        Snackbar.LENGTH_SHORT
    ).apply {
        duration?.let { this.duration = it }
        setAction("Batalkan") { undoAction() }
        show()
    }
}

fun Fragment.showSnackbarWithUndo(message: String?, duration: Int? = null, undoAction: () -> Unit) {
    activity?.showSnackbarWithUndo(message, duration, undoAction)
}

fun postDelayed(delay: Long, action: () -> Unit) {
    Handler().postDelayed({ action() }, delay)
}

fun Context.showShortToast(content: String) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
}


fun Context.showAlertDialogWith2Buttons(
    message: String,
    positiveText: String,
    negativeText: String,
    positiveCallback: ((DialogInterface) -> Unit)? = null,
    negativeCallback: ((DialogInterface) -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
        .setPositiveButton(
            positiveText
        ) { dialog, _ ->
            positiveCallback?.let { it(dialog) }
        }
        .setNegativeButton(
            negativeText
        ) { dialog, _ ->
            negativeCallback?.let { it(dialog) }
        }
    // Create the AlertDialog object and return it
    val dialog = builder.create()
    dialog.show()

    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.accent))
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.accent))
}

fun Context.showAlertDialog(
    message: String,
    btnText: String,
    btnCallback: ((DialogInterface) -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(message)
        .setPositiveButton(
            btnText
        ) { dialog, _ ->
            btnCallback?.let { it(dialog) }
        }

    // Create the AlertDialog object and return it
    val dialog = builder.create()
    dialog.show()

    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.accent))
}
