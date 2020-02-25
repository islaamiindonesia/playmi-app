package com.example.playmi.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playmi.R
import com.example.playmi.ui.auth.LoginActivity
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        btnNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
