package id.islaami.playmi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseUser
import id.islaami.playmi.ui.auth.LoginActivity
import id.islaami.playmi.ui.auth.UserAuthViewModel
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.createClickableString
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.verification_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerificationActivity : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceiveBroadcast(intent)
        }
    }

    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verification_activity)

        email = intent.getStringExtra("EMAIL") ?: ""

        viewModel.initVerificationActivity()
        observeVerifyResult()
        observeResendCode()

        btnVerify.setOnClickListener {
            viewModel.verifyUser(email, inputNumber.text.toString())
        }

        btnResend.apply {
            text = createClickableString(
                context = this@VerificationActivity,
                backgroundColor = R.color.white,
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
                    handleApiError(result.message) { message ->
                        when (message) {
                            "WRONG_VERIFICATION_NUMBER" -> showSnackbar("Nomor tidak valid")
                            else -> showAlertDialog(
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
                    showShortToast("Kode Verifikasi sudah terkirim ulang")
                }
                ERROR -> {
                    handleApiError(result.message) {
                        showSnackbar("Gagal kirim ulang Kode Verifikasi")
                    }
                }
            }
        })
    }

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("SEND_INQUIRY_DATA"))
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(receiver)
    }

    private fun onReceiveBroadcast(intent: Intent) {
        // Get extra data included in the Intent
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
