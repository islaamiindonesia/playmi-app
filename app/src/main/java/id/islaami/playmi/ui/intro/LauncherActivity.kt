package id.islaami.playmi.ui.intro

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate.*
import id.islaami.playmi.R
import id.islaami.playmi.ui.MainActivity
import id.islaami.playmi.ui.auth.LoginActivity
import id.islaami.playmi.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherActivity : BaseActivity() {
    private val viewModel: IntroViewModel by viewModel()

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_actvitiy)

        handler = Handler()

        if (viewModel.darkMode > 0) {
            setDefaultNightMode(viewModel.darkMode)
        } else {
            setDefaultNightMode(MODE_NIGHT_NO)
            viewModel.darkMode = getDefaultNightMode()
        }

        handler.postDelayed({
            if (viewModel.isLoggedIn()) {
                MainActivity.startActivityClearTask(this)
            } else {
                LoginActivity.startActivityClearTask(this)
            }

            finish()
        }, 3000L)
    }
}
