package id.islaami.playmi.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity

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
