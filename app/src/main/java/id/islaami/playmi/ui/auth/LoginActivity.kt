package id.islaami.playmi.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.iid.FirebaseInstanceId
import id.islaami.playmi.R
import id.islaami.playmi.VerificationActivity
import id.islaami.playmi.ui.MainActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.login_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity(
    var audId: String = "",
    var token: String = "",
    var user: FirebaseUser? = null,
    var googleClient: GoogleSignInClient? = null,
    var firebaseAuth: FirebaseAuth? = null,
    var isGoogle: Boolean = false
) : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        firebaseAuth = FirebaseAuth.getInstance()
        googleClient = GoogleSignIn.getClient(this, gso)

        getInstanceID()

        viewModel.initLoginActivity()
        checkIfSessionIsExpired()
        observeLoginResult()
        observeResendCode()

        btnGoogle.setOnClickListener {
            progressBar.setVisibilityToVisible()
            btnLogin.setVisibilityToGone()

            startActivityForResult(googleClient?.signInIntent, GOOGLE_CODE)
        }

        btnForgot.setOnClickListener {
            ForgotPasswordActivity.startActivity(this)
        }

        btnLogin.setOnClickListener {
            if (isFormValid()) firebaseAuthWithPassword()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(task.getResult(ApiException::class.java))
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                showShortToast(getString(R.string.error_message_default))
            }
        }
    }

    /* CUSTOM METHOD */
    private fun getInstanceID() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                audId = task.result?.id.toString()
                token = task.result?.token.toString()
            })
    }

    private fun firebaseAuthWithPassword() {
        progressBar.setVisibilityToVisible()
        btnLogin.setVisibilityToGone()

        firebaseAuth?.signInWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        )?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                isGoogle = false
                viewModel.login(email.text.toString(), token)
            } else {
                btnLogin.setVisibilityToVisible()
                progressBar.setVisibilityToGone()

                // If sign in fails, display a message to the user.
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    showShortToast(getString(R.string.invalid_credentials))
                } catch (e: FirebaseAuthInvalidUserException) {
                    showDialogRegisterUser()
                } catch (e: Exception) {
                    showShortToast(getString(R.string.error_message_default))
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        firebaseAuth?.signInWithCredential(GoogleAuthProvider.getCredential(account?.idToken, null))
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user = firebaseAuth?.currentUser
                    isGoogle = true
                    viewModel.login(user?.email.toString(), token)
                } else {
                    btnLogin.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    // If sign in fails, display a message to the user.
                    showShortToast(getString(R.string.error_message_default))
                }
            }
    }

    private fun checkIfSessionIsExpired() {
        if (intent.getBooleanExtra("EXTRA_INVALID_TOKEN", false)) {
            viewModel.afterLogout()
            showAlertDialog("Sesi Anda telah habis, silahkan login kembali", "OK") { it.dismiss() }
        }
    }

    private fun validateEmail(): Boolean {
        if (email.text.isNotEmpty()) return true

        email.error = "Masukkan email Anda"
        return false
    }

    private fun validatePassword(): Boolean {
        if (password.text.isNotEmpty()) return true

        email.error = "Masukkan password Anda"
        return false
    }

    private fun isFormValid() = validateEmail() && validatePassword()

    private fun showDialogRegisterUser() {
        AccountNotFoundDialogFragment.show(
            fragmentManager = supportFragmentManager,
            email = email.text.toString(),
            btnCancel = "Batal",
            btnOk = "Buat Akun",
            okCallback = {
                RegisterActivity.startActivity(
                    this,
                    audId,
                    token,
                    email.text.toString(),
                    password.text.toString()
                )
            }
        )
    }

    companion object {
        const val GOOGLE_CODE = 700

        fun startActivity(context: Context?) {
            context?.startActivity(
                Intent(context, LoginActivity::class.java)
            )
        }

        fun startActivityWhenErrorInvalidToken(context: Context?) {
            context?.startActivity(
                Intent(context, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("EXTRA_INVALID_TOKEN", true)
            )
        }

        fun startActivityClearTask(context: Context?) {
            context?.startActivity(
                Intent(context, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
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

                    MainActivity.startActivityClearTask(this)
                }
                ERROR -> {
                    btnLogin.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()
                    when (result.message) {
                        "UNVERIFIED" -> {
                            viewModel.resendCode(email.text.toString(), token)
                        }
                        "EMAIL_NOT_FOUND" -> {
                            if (isGoogle) CompleteProfileActivity.startActivity(this, user, token)
                            else showDialogRegisterUser()
                        }
                        else -> {
                            handleApiError(result.message) { showShortToast(it) }
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
                    progressBar.setVisibilityToVisible()
                    btnLogin.setVisibilityToGone()
                }
                SUCCESS -> {
                    progressBar.setVisibilityToGone()
                    btnLogin.setVisibilityToVisible()

                    VerificationActivity.startActivityClearTask(this, email.text.toString(), token)
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnLogin.setVisibilityToVisible()

                    when (result.message) {
                        "EMAIL_NOT_FOUND" -> {
                            if (isGoogle) CompleteProfileActivity.startActivity(this, user, token)
                            else showDialogRegisterUser()
                        }
                        else -> {
                            handleApiError(result.message) { showShortToast(it) }
                        }
                    }
                }
            }
        })
    }
}
