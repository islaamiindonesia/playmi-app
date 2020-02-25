package com.example.playmi.ui.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.playmi.R
import com.example.playmi.ui.MainActivity
import com.example.playmi.ui.auth.LoginActivity
import com.example.playmi.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherActivity : BaseActivity() {

    private val viewModel: IntroViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_actvitiy)

        if (viewModel.hasSeenIntro) {
            if (viewModel.isLoggedIn()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            viewModel.hasSeenIntro = true
            startActivity(Intent(this, IntroActivity::class.java))
        }

        finish()
    }
}
