package id.islaami.playmi.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.jakewharton.rxbinding3.widget.textChanges
import id.islaami.playmi.R
import id.islaami.playmi.VerificationActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.fromAppsFormatDateToDbFormatDate
import id.islaami.playmi.util.fromDbFormatDateToAppsFormatDate
import id.islaami.playmi.util.isValidEmail
import id.islaami.playmi.util.isValidPassword
import id.islaami.playmi.util.ui.*
import io.reactivex.Observable
import io.reactivex.functions.Function6
import kotlinx.android.synthetic.main.register_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class RegisterActivity(var gender: String = "L") : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        setupToolbar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.accent_dark)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0
        }

        firebaseAuth = FirebaseAuth.getInstance()

        viewModel.initRegisterAcitivity()
        observeRegister()

        setupBirthDateDatePickerListener()
        setupForm(intent.getStringExtra("NOTIF_TOKEN"))

        radioGender.setOnCheckedChangeListener { _, i ->
            gender = when (i) {
                R.id.radioMale -> "L"
                else -> "P"
            }
        }

        val emptyFieldStream: Observable<Boolean> = Observable.combineLatest(
            etName.textChanges().map { it.isNotEmpty() },
            etEmail.textChanges().map { it.toString().isValidEmail() },
            etConfirmEmail.textChanges().map { it.toString().isValidEmail() },
            etBirtdate.textChanges().map { it.isNotEmpty() },
            etPassword.textChanges().map { it.toString().isValidPassword() },
            etConfirm.textChanges().map { it.toString().isValidPassword() },
            Function6 { t1, t2, t3, t4, t5, t6 ->
                return@Function6 t1 && t2 && t3 && t4 && t5 && t6
            }
        )

        disposable.add(emptyFieldStream.subscribe { isAllFieldValid ->
            btnRegister.isEnabled = isAllFieldValid
        })
    }

    private fun setupForm(notifToken: String?) {
        etEmail.setText(intent.getStringExtra(EMAIL) ?: "")
        etPassword.setText(intent.getStringExtra(PASSWORD) ?: "")

        btnRegister.setOnClickListener {
            if (validateAll()) {
                firebaseAuthWithPassword(notifToken.toString())
            }
        }
    }

    private fun validateAll() =
        validateConfirmEmail() && validateConfirmPassword() &&
                validatePasswordLength() && validateConfirmPasswordLength()

    private fun validateConfirmPassword(): Boolean {
        return etConfirm.validate(
            layoutEtConfirm,
            "Konfirmasi kata sandi harus sama"
        ) { it.isNotEmpty() && it == etPassword.text.toString() }
    }

    private fun validatePasswordLength(): Boolean {
        return etPassword.validate(
            layoutEtPassword,
            "Kata Sandi harus lebih dari 6 karakter"
        ) { it.isValidPassword() }
    }

    private fun validateConfirmPasswordLength(): Boolean {
        return etConfirm.validate(
            layoutEtConfirm,
            "Kata Sandi harus lebih dari 6 karakter"
        ) { it.isValidPassword() }
    }

    private fun validateConfirmEmail(): Boolean {
        return etConfirmEmail.validate(
            layoutEtConfirmEmail,
            "Konfirmasi email harus sama"
        ) { it.isNotEmpty() && it == etEmail.text.toString() }
    }

    private fun firebaseAuthWithPassword(notifToken: String) {
        progressBar.setVisibilityToVisible()
        btnRegister.setVisibilityToGone()

        firebaseAuth.createUserWithEmailAndPassword(
            etEmail.text.toString(),
            etPassword.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                viewModel.register(
                    fullname = etName.text.toString(),
                    email = etEmail.text.toString(),
                    birthdate = etBirtdate.text.toString().fromAppsFormatDateToDbFormatDate() ?: "",
                    gender = gender,
                    notifToken = notifToken
                )
            } else {
                progressBar.setVisibilityToGone()
                btnRegister.setVisibilityToVisible()

                when (task.exception) {
                    is FirebaseAuthWeakPasswordException -> showShortToast("Kata Sandi kurang dari 6 karakter")
                    is FirebaseAuthUserCollisionException -> showShortToast("Email ${etEmail.text.toString()} sudah digunakan.")
                    else -> {
                        Log.d("HEIKAMU", "firebaseAuthWithPassword: ${task.exception}")
                        showShortToast(getString(R.string.error_message_default))
                    }
                }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val token = intent.getStringExtra("NOTIF_TOKEN")
        VerificationActivity.startActivityClearTask(this, currentUser, token)
    }

    private fun setupBirthDateDatePickerListener() {
        val calendar = Calendar.getInstance()

        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
                day = dayOfMonth
                month = monthOfYear + 1
                year = selectedYear

                val newBirthDate = "$year-$month-$day"

                etBirtdate.setText(newBirthDate.fromDbFormatDateToAppsFormatDate())

            }, year, month, day
        )

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Pilih", datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Batal", datePickerDialog)

        etBirtdate.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                datePickerDialog.show()
            } else {
                datePickerDialog.dismiss()
            }
        }
        etBirtdate.setOnClickListener { datePickerDialog.show() }
    }

    companion object {
        const val AUD_ID = "AUD_ID"
        const val NOTIF_TOKEN = "NOTIF_TOKEN"
        const val EMAIL = "EMAIL"
        const val PASSWORD = "PASSSWORD"

        fun startActivity(
            context: Context?,
            audID: String,
            notifToken: String,
            email: String,
            password: String
        ) {
            context?.startActivity(
                Intent(context, RegisterActivity::class.java)
                    .putExtra(AUD_ID, audID)
                    .putExtra(NOTIF_TOKEN, notifToken)
                    .putExtra(EMAIL, email)
                    .putExtra(PASSWORD, password)
            )
        }
    }

    private fun observeRegister() {
        viewModel.registerResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnRegister.setVisibilityToGone()
                }
                SUCCESS -> {
                    progressBar.setVisibilityToGone()
                    btnRegister.setVisibilityToVisible()
                    updateUI(firebaseAuth.currentUser)
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnRegister.setVisibilityToVisible()
                    showSnackbar(result.message)
                }
            }
        })
    }
}
