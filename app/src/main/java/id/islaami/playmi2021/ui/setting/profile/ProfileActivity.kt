package id.islaami.playmi2021.ui.setting.profile

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.profile.Profile
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.ui.setting.change_password.ChangePasswordActivity
import id.islaami.playmi2021.util.*
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.etBirtdate
import kotlinx.android.synthetic.main.activity_profile.etEmail
import kotlinx.android.synthetic.main.activity_profile.etName
import kotlinx.android.synthetic.main.activity_profile.progressBar
import kotlinx.android.synthetic.main.activity_profile.radioFemale
import kotlinx.android.synthetic.main.activity_profile.radioGender
import kotlinx.android.synthetic.main.activity_profile.radioMale
import kotlinx.android.synthetic.main.activity_profile.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : BaseSpecialActivity() {
    private val viewModel: ProfileViewModel by viewModel()
    var gender = "L"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (viewModel.getProfile() != null) {
            viewModel.getProfile()?.showData()
        } else {
            viewModel.getProfileDetail()
        }

        setupBirthDateDatePickerListener()
        observeProfile()
        observeUpdateProfile()
        swipeRefreshLayout.setOnRefreshListener { refresh() }

        btnSave.setOnClickListener {
            viewModel.updateProfile(
                etName.text.toString(),
                etEmail.text.toString(),
                etBirtdate.text.toString().fromAppsFormatDateToDbFormatDate().toString(),
                gender,
            )
        }

        radioGender.setOnCheckedChangeListener { _, i ->
            gender = when (i) {
                R.id.radioMale -> "L"
                else -> "P"
            }
        }

        btnChangePassword.setOnClickListener {
            ChangePasswordActivity.startActivity(this, etEmail.text.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
                    setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            this@ProfileActivity,
                            R.drawable.bg_dialog_date_picker
                        )
                    )
                }
            } else {
                datePickerDialog.dismiss()
            }
        }
        etBirtdate.setOnClickListener { datePickerDialog.show() }
    }

    private fun Profile?.showData() {
        etName.setText(this?.fullname)
        etBirtdate.setText(this?.birthdate.fromDbFormatDateToAppsFormatDate())
        etEmail.setText(this?.email)
        this?.gender?.let {
            this@ProfileActivity.gender = it
            if (it == "L") {
                radioMale.isChecked = true
                radioFemale.isChecked = false
            } else {
                radioMale.isChecked = false
                radioFemale.isChecked = true
            }
        }
    }

    private fun refresh() {
        viewModel.getProfileDetail()
    }

    private fun observeUpdateProfile() {
        viewModel.updateProfileResultLd.observe(this) { result ->
            when (result.status) {
                ResourceStatus.LOADING -> {
                    progressBar.isVisible = true
                }
                ResourceStatus.SUCCESS -> {
                    progressBar.isVisible = false
                    refresh()
                }
                ResourceStatus.ERROR -> {
                    progressBar.isVisible = false
                }
            }
        }
    }

    private fun observeProfile() {
        viewModel.profileResultLd.observe(this) { result ->
            when (result.status) {
                ResourceStatus.LOADING -> swipeRefreshLayout.startRefreshing()
                ResourceStatus.SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()
                    result.data?.showData()
                }
                ResourceStatus.ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    when (result.message) {
                        ERROR_CONNECTION -> {
                            RefreshDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = getString(R.string.error_connection),
                                btnOk = "Coba Lagi",
                                okCallback = { refresh() },
                                outsideTouchCallback = { refresh() }
                            )
                        }
                        ERROR_CONNECTION_TIMEOUT -> {
                            RefreshDialogFragment.show(
                                fragmentManager = supportFragmentManager,
                                text = getString(R.string.error_connection_timeout),
                                btnOk = "Coba Lagi",
                                okCallback = { refresh() },
                                outsideTouchCallback = { refresh() }
                            )
                        }
                        else -> {
                            handleApiError(errorMessage = result.message) { message ->
                                showLongToast(message)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}