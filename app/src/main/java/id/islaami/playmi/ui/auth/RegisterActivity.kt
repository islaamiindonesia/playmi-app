package id.islaami.playmi.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import id.islaami.playmi.R
import id.islaami.playmi.VerificationActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.Clickable
import id.islaami.playmi.util.ERROR_EMAIL_IN_USE
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.fromAppsFormatDateToDbFormatDate
import id.islaami.playmi.util.fromDbFormatDateToAppsFormatDate
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.register_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RegisterActivity : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    lateinit var firebaseAuth: FirebaseAuth

    lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        setupToolbar(toolbar)

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

        textAgreement.apply {
            text = createTextAgreement()
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun createTextAgreement(): SpannableString {
        val fulltext = getText(R.string.text_agreement) as SpannedString
        val spannableString = SpannableString(fulltext)
        val annotations = fulltext.getSpans(0, fulltext.length, Annotation::class.java)
        annotations.find {
            it.value == "tnc"
        }.let {
            spannableString.apply {
                setSpan(
                    Clickable(1) { showShortToast("tnc") },
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.NORMAL),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.white
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
            }
        }
        annotations.find {
            it.value == "privacy"
        }.let {
            spannableString.apply {
                setSpan(
                    Clickable(2) { showShortToast("privacy") },
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.NORMAL),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.white
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
            }
        }

        return spannableString
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

    private fun setupForm(notifToken: String?) {
        btnRegister.setOnClickListener {
            if (isFormValid()) {
                firebaseAuthWithPassword(notifToken.toString())
            } else {
                showSnackbar("Mohon Lengkapi Data Akun Anda")
            }
        }
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
                    birthdate = etBirtdate.text.toString().fromAppsFormatDateToDbFormatDate()
                        .toString(),
                    gender = gender,
                    notifToken = notifToken
                )
            } else {
                when (task.exception?.message) {
                    ERROR_EMAIL_IN_USE -> showSnackbar("Email ${etEmail.text.toString()} sudah digunakan.")
                }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val token = intent.getStringExtra("NOTIF_TOKEN")
        VerificationActivity.startActivity(this, currentUser, token)
    }

    private fun setupBirthDateDatePickerListener() {
        val calendar = Calendar.getInstance()

        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { datePicker, selectedYear, monthOfYear, dayOfMonth ->
                day = dayOfMonth
                month = monthOfYear + 1
                year = selectedYear

                val newBirthDate = "$year-$month-$day"

                etBirtdate.setText(newBirthDate.fromDbFormatDateToAppsFormatDate())

            }, year, month, day
        )

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Pilih", datePickerDialog)
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Batal", datePickerDialog)

        etBirtdate.setOnClickListener { datePickerDialog.show() }
    }

    private fun isFormValid() =
        etName.validate(layoutEtName, "Nama tidak boleh kosong") &&
                etEmail.validate(layoutEtEmail, "Email tidak boleh kosong") &&
                etPassword.validate(layoutEtPassword, "Kata Sandi tidak boleh kosong") &&
                etBirtdate.validate(layoutEtBirthdate, "Tanggal Lahir tidak boleh kosong")

    companion object {
        fun startActivity(context: Context?, audID: String, notifToken: String) {
            context?.startActivity(
                Intent(context, RegisterActivity::class.java)
                    .putExtra("AUD_ID", audID)
                    .putExtra("NOTIF_TOKEN", notifToken)
            )
        }
    }
}
