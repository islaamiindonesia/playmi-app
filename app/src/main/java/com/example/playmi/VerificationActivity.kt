package com.example.playmi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.playmi.ui.auth.RegisterActivity
import com.example.playmi.ui.auth.UserAuthViewModel
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToGone
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToVisible
import kotlinx.android.synthetic.main.verification_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerificationActivity : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_activity)

        email = intent.getStringExtra("EMAIL") ?: ""

        viewModel.initVerificationActivity()
        observeVerifyResult()

        btnVerify.setOnClickListener {
            viewModel.verifyUser(email, inputNumber.text.toString())
        }
    }

    private fun observeVerifyResult() {
        viewModel.verificationResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnResend.setVisibilityToGone()
                }
                SUCCESS -> {
                    progressBar.setVisibilityToGone()
                    btnResend.setVisibilityToVisible()
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnResend.setVisibilityToVisible()
                }
            }
        })
    }

    companion object {
        fun startActivity(context: Context?, email: String) {
            context?.startActivity(
                Intent(context, VerificationActivity::class.java)
                    .putExtra("EMAIL", email)
            )
        }
    }
}
