package id.islaami.playmi2021.util.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoast.StyleableToast
import id.islaami.playmi2021.R
import id.islaami.playmi2021.util.STORAGE_URL
import kotlinx.android.synthetic.main.login_activity.*

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

fun Context.createMaterialAlertDialog(
    positive: String,
    positiveCallback: (() -> Unit)? = null,
    dismissCallback: (() -> Unit)? = null
): AlertDialog {
    val alertDialog = MaterialAlertDialogBuilder(this, R.style.PlaymiMaterialDialog)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alertDialog.background = getDrawable(R.drawable.bg_dialog)
    }

    alertDialog.setPositiveButton(positive) { dialogInterface, _ ->
        positiveCallback?.invoke()
        dialogInterface.dismiss()
    }
    alertDialog.setNegativeButton("Tutup") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }

    alertDialog.setOnDismissListener { dismissCallback?.invoke() }

    return alertDialog.create()
}

fun Context.showMaterialAlertDialog(dialog: AlertDialog, message: String) {
    dialog.setMessage(message)
    dialog.show()

    val positiveBtn = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
    positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    positiveBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))

    val negativeBtn = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
    negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.text_color))
}

fun Fragment.showMaterialAlertDialog(
    context: Context?,
    dialog: AlertDialog,
    message: String
) {
    context?.showMaterialAlertDialog(dialog, message)
}

fun Context.showMaterialAlertDialog(
    message: String,
    positive: String,
    positiveCallback: (() -> Unit)? = null,
    dismissCallback: (() -> Unit)? = null
) {
    val alertDialog = MaterialAlertDialogBuilder(this, R.style.PlaymiMaterialDialog)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alertDialog.background = getDrawable(R.drawable.bg_dialog)
    }
    alertDialog.setMessage(message)
    alertDialog.setPositiveButton(positive) { dialogInterface, _ ->
        positiveCallback?.invoke()
        dialogInterface.dismiss()
    }
    alertDialog.setNegativeButton("Tutup") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }

    alertDialog.setOnDismissListener { dismissCallback?.invoke() }
    val dialog = alertDialog.create()

    dialog.show()

    val positiveBtn = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
    positiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    positiveBtn.setTextColor(ContextCompat.getColor(this, R.color.accent))

    val negativeBtn = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
    negativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.text_color))
}

fun Fragment.showMaterialAlertDialog(
    context: Context?,
    message: String,
    positive: String,
    positiveCallback: (() -> Unit)? = null,
    dismissCallback: (() -> Unit)? = null
) {
    context?.showMaterialAlertDialog(message, positive, positiveCallback, dismissCallback)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ImageView.loadImage(url: String?) = Glide.with(this).load("$STORAGE_URL$url").into(this)

fun ImageView.loadExternalImage(url: String?) = Glide.with(this).load(url).into(this)

fun ImageView.loadYoutubeThumbnail(videoId: String) {
    val url = "https://img.youtube.com/vi/$videoId/"
    Glide.with(this).load(url+"maxresdefault.jpg").error(Glide.with(this).load(url+"mqdefault.jpg")).into(this)
}

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

fun Context.showLongToast(content: String?) {
    StyleableToast.makeText(
        this, content,
        Toast.LENGTH_LONG, R.style.PlaymiToast
    ).show()
}

fun Fragment.showLongToast(context: Context?, content: String?) {
    context?.showLongToast(content)
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
