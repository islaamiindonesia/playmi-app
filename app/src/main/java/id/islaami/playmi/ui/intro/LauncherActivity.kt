package id.islaami.playmi.ui.intro

import android.os.Bundle
import android.os.Handler
import com.google.android.gms.ads.MobileAds
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

        MobileAds.initialize(this)

        handler = Handler()

        handler.postDelayed({
            if (viewModel.hasSeenIntro) {
                if (viewModel.isLoggedIn()) {
                    MainActivity.startActivityClearTask(this)
                } else {
                    LoginActivity.startActivityClearTask(this)
                }
            } else {
                IntroActivity.startActivity(this)
                viewModel.hasSeenIntro = true
            }

            finish()
        }, 3000L)
    }
}
