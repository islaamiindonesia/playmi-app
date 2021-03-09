package id.islaami.playmi2021.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity

class ResetPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_activity)
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(
                Intent(context, ResetPasswordActivity::class.java)
            )
        }
    }
}
