package id.islaami.playmi2021.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_underage.*

class UnderageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_underage)

        btnCloseApp.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onBackPressed() {
    }

    companion object {
        fun startActivityClearTask(context: Context?) {
            context?.startActivity(
                Intent(context, UnderageActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}