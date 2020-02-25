package com.example.playmi.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.playmi.R
import com.example.playmi.VerificationActivity
import com.example.playmi.ui.MainActivity
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.ui.CustomDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToGone
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToVisible
import kotlinx.android.synthetic.main.login_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private val viewModel: UserAuthViewModel by viewModel()

    lateinit var audId: String
    lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("HEIKAMU", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                audId = task.result?.id.toString()
                token = task.result?.token.toString()
            })

        viewModel.initLoginActivity()

        observeLoginResult()

        btnLogin.setOnClickListener {
            validateLogin()
        }
    }

    private fun validateLogin() {
        if (isFormValid()) {
            viewModel.login(
                inputEmail.text.toString(),
                inputPassword.text.toString()
            )
        }
    }

    private fun observeLoginResult() {
        // observe login result
        viewModel.loginResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnLogin.setVisibilityToGone()
                }
                SUCCESS -> {
                    btnLogin.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()
                    MainActivity.startActivityAfterLogin(this)
                }
                ERROR -> {
                    btnLogin.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()
                    when (result.message) {
                        "EMAIL_NOT_FOUND" -> {
                            CustomDialogFragment.show(
                                supportFragmentManager,
                                text = "Email ${inputEmail.text} belum terdaftar",
                                btnCancel = "Batal",
                                btnOk = "Daftar Akun",
                                okCallback = { RegisterActivity.startActivity(this, audId, token) }
                            )
                        }
                        "UNVERIFIED" -> {
                            CustomDialogFragment.show(
                                supportFragmentManager,
                                text = "Email Anda belum diverifikasi",
                                btnCancel = "Batal",
                                btnOk = "Verifikasi",
                                okCallback = {
                                    VerificationActivity.startActivity(
                                        this,
                                        inputEmail.text.toString()
                                    )
                                }
                            )
                        }
                        else -> {
                            CustomDialogFragment.show(
                                supportFragmentManager,
                                text = "Email/Kata Sandi Anda salah",
                                btnCancel = "Batal",
                                btnOk = "Ok"
                            )
                        }
                    }
                }
            }
        })
    }

    private fun validateEmail(): Boolean {
        if (inputEmail.text.isNotEmpty()) return true

        inputEmail.error = "Masukkan email Anda"
        return false
    }

    private fun validatePassword(): Boolean {
        if (inputPassword.text.isNotEmpty()) return true

        inputEmail.error = "Masukkan password Anda"
        return false
    }

    private fun isFormValid() = validateEmail() && validatePassword()

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(
                Intent(context, LoginActivity::class.java)
            )
        }

        fun startActivityAfterLogout(context: Context?) {
            context?.startActivity(
                Intent(context, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
