package id.islaami.playmi.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.islaami.playmi.R
import id.islaami.playmi.ui.auth.LoginActivity
import kotlinx.android.synthetic.main.intro_activity.*

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        btnNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
