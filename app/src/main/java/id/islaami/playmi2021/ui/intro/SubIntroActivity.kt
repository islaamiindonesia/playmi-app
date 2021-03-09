package id.islaami.playmi2021.ui.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.islaami.playmi2021.R

class SubIntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_intro)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        val title = intent.getStringExtra("title")
        supportActionBar?.title = title
    }
}
