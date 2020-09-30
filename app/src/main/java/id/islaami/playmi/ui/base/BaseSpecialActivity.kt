package id.islaami.playmi.ui.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import id.islaami.playmi.R
import id.islaami.playmi.config.injectFeature
import io.reactivex.disposables.CompositeDisposable

abstract class BaseSpecialActivity : AppCompatActivity() {
    lateinit var disposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
        disposable = CompositeDisposable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // set status bar color to accent dark
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.accent_dark)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // set status bar text color to white (using dark mode)
            window.decorView.systemUiVisibility = 0
        }
    }
}