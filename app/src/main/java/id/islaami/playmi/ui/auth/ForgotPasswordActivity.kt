package id.islaami.playmi.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.ui.setVisibilityToVisible
import id.islaami.playmi.util.ui.showShortToast
import kotlinx.android.synthetic.main.forgot_password_activity.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        val auth = FirebaseAuth.getInstance()

        btnSend.setOnClickListener {
            btnSend.setVisibilityToGone()
            progressBar.setVisibilityToVisible()

            auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    if (task.isSuccessful) {
                        layoutField.setVisibilityToGone()
                        textTitle.text = "Cek Email Anda!"
                        textSubtitle.text =
                            "Instruksi pengaturan ulang kata sandi\nsudah dikirimkan ke email Anda"
                    } else {
                        showShortToast(getString(R.string.error_message_default))
                    }
                }
        }

        linkLogin.setOnClickListener { onBackPressed() }
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }
    }
}
