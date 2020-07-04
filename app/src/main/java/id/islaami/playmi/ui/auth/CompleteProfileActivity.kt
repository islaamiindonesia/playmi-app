package id.islaami.playmi.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseUser
import id.islaami.playmi.R
import id.islaami.playmi.ui.MainActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.BUNDLE
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.fromAppsFormatDateToDbFormatDate
import id.islaami.playmi.util.fromDbFormatDateToAppsFormatDate
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.ui.setVisibilityToVisible
import id.islaami.playmi.util.ui.showShortToast
import id.islaami.playmi.util.ui.showSnackbar
import kotlinx.android.synthetic.main.complete_profile_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CompleteProfileActivity(var gender: String = "") : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.complete_profile_activity)

        val user = intent.getBundleExtra(BUNDLE)?.getParcelable<FirebaseUser>("USER")
        val notifToken = intent.getStringExtra("NOTIF_TOKEN")

        viewModel.initCompleteProfileActvitiy()
        observeRegister()

        setupBirthDateDatePickerListener()

        radioGender.setOnCheckedChangeListener { _, i ->
            gender = when (i) {
                R.id.radioMale -> "L"
                else -> "P"
            }
        }

        btnSave.setOnClickListener {
            handleUser(user, notifToken)
        }
    }

    private fun handleUser(user: FirebaseUser?, notifToken: String?) {
        progressBar.setVisibilityToVisible()
        btnSave.setVisibilityToGone()

        user?.updatePassword(etPassword.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val birthDate = etBirtdate.text.toString()

                    viewModel.registerFromGoogle(
                        fullname = user.displayName.toString(),
                        email = user.email.toString(),
                        birthdate = birthDate.fromAppsFormatDateToDbFormatDate().toString(),
                        gender = gender,
                        notifToken = notifToken.toString()
                    )
                } else {
                    progressBar.setVisibilityToGone()
                    btnSave.setVisibilityToVisible()

                    showShortToast(getString(R.string.error_message_default))
                }
            }
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

        etBirtdate.setOnClickListener { datePickerDialog.show() }
    }


    companion object {
        fun startActivity(context: Context, user: FirebaseUser?, token: String) {
            context.startActivity(
                Intent(context, CompleteProfileActivity::class.java).apply {
                    putExtra(BUNDLE, Bundle().apply {
                        putParcelable("USER", user)
                    })
                    putExtra("NOTIF_TOKEN", token)
                }
            )
        }
    }

    private fun observeRegister() {
        viewModel.loginResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnSave.setVisibilityToGone()
                }
                SUCCESS -> {
                    progressBar.setVisibilityToGone()
                    btnSave.setVisibilityToVisible()

                    MainActivity.startActivityClearTask(this)
                }
                ERROR -> {
                    progressBar.setVisibilityToGone()
                    btnSave.setVisibilityToVisible()

                    handleApiError(result.message) { showShortToast(it) }
                }
            }
        })
    }
}
