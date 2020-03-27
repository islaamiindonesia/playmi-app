package id.islaami.playmi.util.ui

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * Created by Kemal Amru Ramadhan on 09/04/2019.
 */

/**
 * @param statusBarColor int color from ContextCompat.getColor() or getResources.getColor()
 */
fun Activity.setStatusBarColor(statusBarColor: Int, isDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = statusBarColor

        setStatusBarTheme(isDark)
    }
}

private fun Activity.setStatusBarTheme(isDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Fetch the current flags.
        val lFlags = window.decorView.systemUiVisibility

        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        window.decorView.systemUiVisibility =
            if (isDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}
