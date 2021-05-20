package id.islaami.playmi2021.ui.setting.change_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.util.isValidPassword
import id.islaami.playmi2021.util.ui.showLongToast
import id.islaami.playmi2021.util.ui.validate
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.progressBar
import kotlinx.android.synthetic.main.activity_change_password.toolbar

class ChangePasswordActivity : BaseSpecialActivity() {
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.getStringExtra(EXTRA_EMAIL) == null) {
            finish()
        }
        email = intent.getStringExtra(EXTRA_EMAIL)!!

        btnSave.setOnClickListener {
            if (validateAll()) {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        progressBar.isVisible = true
        val user = FirebaseAuth.getInstance().currentUser

        val newCredential = EmailAuthProvider.getCredential(email, etPasswordOld.text.toString())

        user?.reauthenticate(newCredential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    user.updatePassword(etPasswordNew.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            progressBar.isVisible = false
                            finish()
                        } else {
                            progressBar.isVisible = false
                            showLongToast("Terjadi kesalahan, coba lagi")
                        }
                    }
                } else {
                    progressBar.isVisible = false
                    showLongToast("Kata sandi saat ini salah, coba lagi")
                }
            }
    }
    private fun validateAll() = validatePasswordOldLength() && validatePasswordNewLength() && validateConfirmPassword()

    private fun validatePasswordOldLength(): Boolean {
        return etPasswordOld.validate(
            layoutEtPasswordOld,
            "Kata Sandi harus lebih dari 6 karakter"
        ) { it.isValidPassword() }
    }
    private fun validatePasswordNewLength(): Boolean {
        return etPasswordNew.validate(
            layoutEtPasswordNew,
            "Kata Sandi harus lebih dari 6 karakter"
        ) { it.isValidPassword() }
    }

    private fun validateConfirmPassword(): Boolean {
        return etPasswordConfirm.validate(
            layoutEtPasswordConfirm,
            "Konfirmasi kata sandi harus sama"
        ) { it.isNotEmpty() && it == etPasswordNew.text.toString() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"

        fun startActivity(context: Context, email: String) {
            context.startActivity(Intent(context, ChangePasswordActivity::class.java).apply {
                putExtra(EXTRA_EMAIL, email)
            })
        }
    }
}