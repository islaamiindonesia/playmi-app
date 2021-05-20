package id.islaami.playmi2021.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.util.ui.setVisibilityToGone
import id.islaami.playmi2021.util.ui.setVisibilityToVisible
import id.islaami.playmi2021.util.ui.showLongToast
import kotlinx.android.synthetic.main.forgot_password_activity.*
import kotlinx.android.synthetic.main.forgot_password_activity.progressBar

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        val fromProfile = intent.getBooleanExtra(EXTRA_FROM_PROFILE, false)
        val emailStr = intent.getStringExtra(EXTRA_EMAIL)

        if (fromProfile) {
            linkLogin.text = "Batal"
        }

        email.setText(emailStr)

        btnSend.setOnClickListener {
            btnSend.setVisibilityToGone()
            progressBar.setVisibilityToVisible()

            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    if (task.isSuccessful) {
                        layoutField.setVisibilityToGone()
                        textTitle.text = "Cek Email Anda!"
                        textSubtitle.text =
                            "Instruksi pengaturan ulang kata sandi\nsudah dikirimkan ke email Anda"
                        if (fromProfile) {
                            linkLogin.text = "kembali ke halaman akun"
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            showLongToast(
                                getString(
                                    R.string.error_email_not_found,
                                    email.text.toString()
                                )
                            )
                        } catch (e: Exception) {
                            showLongToast(getString(R.string.error_message_default))
                        }
                    }
                }
        }

        linkLogin.setOnClickListener { onBackPressed() }
    }

    companion object {
        const val EXTRA_FROM_PROFILE = "extra_from_profile"
        const val EXTRA_EMAIL = "extra_email"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }

        fun startActivityFromProfile(context: Context, email: String) {
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java).apply {
                putExtra(
                    EXTRA_FROM_PROFILE, true
                )
                putExtra(
                    EXTRA_EMAIL, email
                )
            })
        }
    }
}
