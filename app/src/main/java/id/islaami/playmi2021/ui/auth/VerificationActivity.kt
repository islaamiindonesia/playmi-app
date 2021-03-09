package id.islaami.playmi2021.ui.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseUser
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.createClickableString
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.setVisibilityToGone
import id.islaami.playmi2021.util.ui.setVisibilityToVisible
import id.islaami.playmi2021.util.ui.showAlertDialog
import id.islaami.playmi2021.util.ui.showLongToast
import id.islaami.playmi2021.util.value
import kotlinx.android.synthetic.main.verification_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerificationActivity(var email: String = "") : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    // this activity will receive broadcast from MyFirebaseMessagingService class
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceiveBroadcast(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_activity)

        email = intent.getStringExtra("EMAIL") ?: ""

        viewModel.initVerificationActivity()
        observeVerifyResult()
        observeResendCode()

        inputNumber.addTextChangedListener {
            btnVerify.isEnabled = it?.length.value() > 0
        }

        btnVerify.setOnClickListener {
            viewModel.verifyUser(email, inputNumber.text.toString())
        }

        btnResend.apply {
            text = createClickableString(
                context = this@VerificationActivity,
                foregroundColor = R.color.accent,
                isUnderLine = true,
                stringRes = R.string.link_resend_verification,
                key = "resend",
                onClickAction = {
                    val token = intent.getStringExtra("TOKEN")
                    if (!token.isNullOrEmpty()) {
                        viewModel.resendCode(email, token)
                    }
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver()
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

                    LoginActivity.startActivityClearTask(this)
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnResend.setVisibilityToVisible()

                    when (result.message) {
                        "WRONG_VERIFICATION_NUMBER" -> showLongToast("Nomor verifikasi tidak valid")
                        else -> handleApiError(result.message) { message ->
                            showAlertDialog(
                                message = message,
                                btnText = "Coba Lagi",
                                btnCallback = { it.dismiss() }
                            )
                        }
                    }
                }
            }
        })
    }

    private fun observeResendCode() {
        viewModel.resendCodeResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    showLongToast("Kode Verifikasi sudah terkirim ulang")
                }
                ERROR -> {
                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("SEND_INQUIRY_DATA"))
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun onReceiveBroadcast(intent: Intent) {
        val message = intent.getStringExtra("VERIFICATION_CODE") ?: ""

        if (message.isNotEmpty()) {
            inputNumber.setText(message)
        }
    }

    companion object {
        fun startActivityClearTask(context: Context?, email: String, token: String?) {
            context?.startActivity(
                Intent(context, VerificationActivity::class.java)
                    .putExtra("EMAIL", email)
                    .putExtra("TOKEN", token)
            )
        }

        fun startActivityClearTask(context: Context?, user: FirebaseUser?, token: String?) {
            context?.startActivity(Intent(context, VerificationActivity::class.java).apply {
                putExtra("BUNDLE", Bundle().apply {
                    putParcelable("USER", user)
                })
                putExtra("TOKEN", token)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}
