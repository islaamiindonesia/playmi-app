package id.islaami.playmi2021.ui.intro

import android.os.Bundle
import android.os.Handler
import com.google.android.gms.ads.MobileAds
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.kotpref.Default
import id.islaami.playmi2021.ui.MainActivity
import id.islaami.playmi2021.ui.auth.LoginActivity
import id.islaami.playmi2021.ui.base.BaseActivity

class LauncherActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_actvitiy)

        MobileAds.initialize(this)

        Handler().apply {
            postDelayed({
                if (Default.hasSeenIntro) {
                    if (Default.hasLoggedIn) {
                        MainActivity.startActivityClearTask(this@LauncherActivity)
                    } else {
                        LoginActivity.startActivityClearTask(this@LauncherActivity)
                    }
                } else {
                    IntroActivity.startActivityClearTask(this@LauncherActivity)
                }

                finish()
            }, 3000L)
        }
    }
}
