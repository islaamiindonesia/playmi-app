package id.islaami.playmi2021.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.jakewharton.rxbinding3.widget.textChanges
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.kotpref.Default
import id.islaami.playmi2021.ui.MainActivity
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.ui.*
import io.reactivex.Observable
import io.reactivex.functions.Function6
import kotlinx.android.synthetic.main.register_activity.*
import kotlinx.android.synthetic.main.register_activity.etBirtdate
import kotlinx.android.synthetic.main.register_activity.etPassword
import kotlinx.android.synthetic.main.register_activity.layoutEtPassword
import kotlinx.android.synthetic.main.register_activity.progressBar
import kotlinx.android.synthetic.main.register_activity.radioGender
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RegisterActivity(
    var firebaseAuth: FirebaseAuth? = null,
    var gender: String = "L" // default gender choice
) : BaseSpecialActivity() {
    private val viewModel: UserAuthViewModel by viewModel()
    private var userToken: String? = null
    private var idToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        setupToolbar(toolbar)

        // initiate firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance()
        userToken = intent.getStringExtra("NOTIF_TOKEN");
        idToken = intent.getStringExtra(ID_TOKEN);

        viewModel.initRegisterAcitivity()
        observeRegister()

        setupBirthDateDatePickerListener()
        setupForm()

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

    /* CUSTOM METHOD */
    private fun setupForm() {
        etName.setText(intent.getStringExtra(FULLNAME) ?: "")
        etEmail.setText(intent.getStringExtra(EMAIL) ?: "")
        etPassword.setText(intent.getStringExtra(PASSWORD) ?: "")

        btnRegister.setOnClickListener {
            if (validateIsUnderage()) {
                UnderageActivity.startActivityClearTask(this)
                return@setOnClickListener
            }
            if (validateAll()) {
                if ((intent.getStringExtra(FULLNAME) ?: "").isEmpty()) {
                    VerificationActivity.startActivityFromResult(this, etEmail.text.toString(), etName.text.toString())
                } else {
                    firebaseAuth?.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                task.result?.user?.linkWithCredential(EmailAuthProvider.getCredential(etEmail.text.toString(), etPassword.text.toString()))
                                viewModel.register(
                                    fullname = etName.text.toString(),
                                    email = etEmail.text.toString(),
                                    birthdate = etBirtdate.text.toString().fromAppsFormatDateToDbFormatDate()
                                        .toString(),
                                    gender = gender,
                                    notifToken = userToken.toString()
                                )
                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseNetworkException) {
                                    showLongToast(getString(R.string.error_connection))
                                } catch (e: Exception) {
                                    showLongToast(getString(R.string.error_message_default))
                                }
                            }
                        }
                }
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

    private fun validateIsUnderage(): Boolean {
        val today = Calendar.getInstance()
        val dob = etBirtdate.text.toString().fromAppsFormatDateToCalendar() ?: return true
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--

        return age < 17
    }

    private fun firebaseAuthWithPassword(notifToken: String) {
        progressBar.setVisibilityToVisible()
        btnRegister.setVisibilityToGone()

        firebaseAuth?.createUserWithEmailAndPassword(
            etEmail.text.toString(),
            etPassword.text.toString()
        )?.addOnCompleteListener(this) { task ->
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
                    is FirebaseAuthWeakPasswordException -> showLongToast("Kata Sandi kurang dari 6 karakter")
                    is FirebaseAuthUserCollisionException -> showLongToast("Email ${etEmail.text.toString()} sudah digunakan.")
                    is FirebaseNetworkException -> showLongToast(getString(R.string.error_connection))
                    else -> showLongToast(getString(R.string.error_message_default))
                }
            }
        }
    }

    private fun setupBirthDateDatePickerListener() {
        var day = 1
        var month = 0
        var year = 1999

        val datePickerDialog = DatePickerDialog(
            this,
                R.style.MySpinnerDatePickerStyle,
            DatePickerDialog.OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
                day = dayOfMonth
                month = monthOfYear + 1
                year = selectedYear

                val newBirthDate = "$year-$month-$day"

                etBirtdate.setText(newBirthDate.fromDbFormatDateToAppsFormatDate())

            }, year, month, day
        )

        datePickerDialog.setTitle("Pilih Tanggal Lahir")
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Pilih", datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Batal", datePickerDialog)

        etBirtdate.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                datePickerDialog.show()
                datePickerDialog.window?.apply {
                    setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                    setBackgroundDrawable(ContextCompat.getDrawable(this@RegisterActivity, R.drawable.bg_dialog_date_picker))
                }
            } else {
                datePickerDialog.dismiss()
            }
        }
        etBirtdate.setOnClickListener { datePickerDialog.show() }
    }

    companion object {
        const val AUD_ID = "AUD_ID"
        const val NOTIF_TOKEN = "NOTIF_TOKEN"
        const val ID_TOKEN = "ID_TOKEN"
        const val FULLNAME = "FULLNAME"
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

        fun startActivityFromGoogleAccount(
            context: Context?,
            audID: String,
            notifToken: String,
            idToken: String,
            fullName: String,
            email: String
        ) {
            context?.startActivity(
                Intent(context, RegisterActivity::class.java)
                    .putExtra(AUD_ID, audID)
                    .putExtra(NOTIF_TOKEN, notifToken)
                    .putExtra(ID_TOKEN, idToken)
                    .putExtra(FULLNAME, fullName)
                    .putExtra(EMAIL, email)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VerificationActivity.EMAIL_VERIF_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                firebaseAuthWithPassword(userToken.toString())
            } else {
                showLongToast("Email not verified")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun observeRegister() {
        viewModel.loginResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnRegister.setVisibilityToGone()
                }
                SUCCESS -> {
                    progressBar.setVisibilityToGone()
                    btnRegister.setVisibilityToVisible()

                    MainActivity.startActivityClearTask(this)
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnRegister.setVisibilityToVisible()

                    handleApiError(result.message) { showLongToast(it) }
                }
            }
        })
    }
}
