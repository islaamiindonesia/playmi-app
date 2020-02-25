package com.example.playmi.ui.auth

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.playmi.R
import com.example.playmi.VerificationActivity
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import com.example.playmi.util.fromAppsFormatDateToDbFormatDate
import com.example.playmi.util.fromDbFormatDateToAppsFormatDate
import com.example.playmi.util.ui.setupToolbar
import com.example.playmi.util.ui.validate
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToGone
import id.co.badr.commerce.mykopin.util.ui.setVisibilityToVisible
import id.co.badr.commerce.mykopin.util.ui.showSnackbar
import kotlinx.android.synthetic.main.register_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RegisterActivity : BaseActivity() {
    private val viewModel: UserAuthViewModel by viewModel()

    lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        setupToolbar(toolbar)

        viewModel.initRegisterAcitivity()
        observeRegister()

        setupBirthDateDatePickerListener()
        setupForm(intent.getStringExtra("NOTIF_TOKEN"))

        radioGender.setOnCheckedChangeListener { _, i ->
            gender = when (i) {
                R.id.radioMale -> "L"
                else -> "L"
            }
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
                    VerificationActivity.startActivity(this, etEmail.text.toString())
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
            // MainActivity.startActivityAfterLogin(this)
            if (isFormValid()) {
                viewModel.register(
                    fullname = etName.text.toString(),
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString(),
                    birthdate = etBirtdate.text.toString().fromAppsFormatDateToDbFormatDate().toString(),
                    gender = gender,
                    notifToken = notifToken.toString()
                )
            } else {
                showSnackbar("Mohon Lengkapi Data Akun Anda")
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
