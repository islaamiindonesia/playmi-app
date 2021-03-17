package id.islaami.playmi2021.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.showLongToast
import kotlinx.android.synthetic.main.verification_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.text.toDouble

class VerificationActivity() : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    var email: String = ""
    var name: String = ""
    var requestCode: Int = 0
    var verifCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_activity)

        email = intent.getStringExtra("EMAIL") ?: ""
        name = intent.getStringExtra("NAME") ?: ""
        requestCode = intent.getIntExtra("REQUEST_CODE", 0)

        viewModel.initVerificationActivity()
        observeResendCode()

        inputNumber.addTextChangedListener {
            btnVerify.isEnabled = it?.length.value() > 0
        }

        btnVerify.setOnClickListener {
            if (verifCode.toString() == inputNumber.text.toString()) {
                setResult(RESULT_OK)
                finish()
            } else {
                showLongToast("Wrong code!")
            }
        }
        setUpResendCode()
        viewModel.resendCode(email, name)
    }

    private fun setUpResendCode() {
        btnResend.apply {
            text = createClickableString(
                    context = this@VerificationActivity,
                    foregroundColor = R.color.accent,
                    isUnderLine = true,
                    stringRes = R.string.link_resend_verification,
                    key = "resend",
                    onClickAction = {
                        viewModel.resendCode(email, name)
                    }
            )
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun startResendCountDown() {
        object : CountDownTimer(60000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                btnResend.text = createColorizedString(
                        fulltext = SpanFormatter.format(getText(R.string.link_resend_verification_muted), seconds),
                        context = this@VerificationActivity,
                        foregroundColor = R.color.grey_85,
                        key = "resend"
                )
            }

            override fun onFinish() {
                setUpResendCode()
            }
        }.start()
    }

    private fun observeResendCode() {
        viewModel.resendCodeResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    startResendCountDown()
                }
                SUCCESS -> {
                    if (result.data != null) verifCode = result.data.toString().toDouble().toInt()
                    showLongToast("Kode verifikasi sudah terkirim")
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }


    companion object {
        const val EMAIL_VERIF_REQUEST_CODE = 1000;

        fun startActivityFromResult(context: Context?, email: String, name: String) {
            (context as Activity).startActivityForResult(
                    Intent(context, VerificationActivity::class.java)
                            .putExtra("NAME", name)
                            .putExtra("EMAIL", email),
                    EMAIL_VERIF_REQUEST_CODE
            )
        }
    }
}
