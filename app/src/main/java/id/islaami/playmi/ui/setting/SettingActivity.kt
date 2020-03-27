package id.islaami.playmi.ui.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.firebase.iid.FirebaseInstanceId
import id.islaami.playmi.R
import id.islaami.playmi.ui.auth.LoginActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.setting.help.WebHelpActivity
import id.islaami.playmi.ui.setting.insight.InsightActivity
import id.islaami.playmi.ui.setting.policy.PolicyActiviy
import id.islaami.playmi.ui.setting.recommendation.RecommendationActivity
import id.islaami.playmi.ui.setting.report.ReportActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.setting_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SettingActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModel()

    private var profileName = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)

        setupToolbar(toolbar)

        viewModel.initSettingActivity()
        observeProfileName()
        observeLogoutResult()

        val packageInfo = this.packageManager?.getPackageInfo(packageName, 0)
        if (packageInfo != null) {
            version.text = getString(R.string.app_version, packageInfo.versionName)
        }

        stgLanguage.text = Locale(viewModel.selectedLocale).displayLanguage

        setupLocation()
        setupButton()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener { viewModel.getProfileName() }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkAccessLocationPermission()) {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun setupButton() {
        toggleTheme.apply {
            this.isChecked = viewModel.darkMode != MODE_NIGHT_NO
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    setDefaultNightMode(MODE_NIGHT_YES)
                } else {
                    setDefaultNightMode(MODE_NIGHT_NO)
                }
                viewModel.darkMode = getDefaultNightMode()
            }
        }

        btnAccount.setOnClickListener()
        {
            showShortToast("open islaami")
        }

        btnRecommendation.setOnClickListener()
        {
            RecommendationActivity.startActivity(this)
        }

        btnInsight.setOnClickListener()
        {
            InsightActivity.startActivity(this)
        }

        btnReport.setOnClickListener()
        {
            ReportActivity.startActivity(this)
        }

        btnHelp.setOnClickListener()
        {
            WebHelpActivity.startActivity(this, "https://www.google.com")
        }

        btnCollaborate.setOnClickListener()
        {
            PolicyActiviy.startActivity(this, "COOP_PLAYMI")
        }

        btnAbout.setOnClickListener()
        {
            PolicyActiviy.startActivity(this, "ABOUT_PLAYMI")
        }

        btnTNC.setOnClickListener()
        {
            PolicyActiviy.startActivity(this, "TNC_PLAYMI")
        }

        btnPrivacy.setOnClickListener()
        {
            PolicyActiviy.startActivity(this, "PRIVACY_PLAYMI")
        }

        btnLogout.setOnClickListener()
        {
            viewModel.logout()
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            getCityName(location.latitude, location.longitude)
        }
    }

    private fun setupLocation() {
        geocoder = Geocoder(this, Locale.getDefault())

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    private fun getCityName(latitude: Double, longitude: Double) {
        val address = geocoder.getFromLocation(latitude, longitude, 1)

        if (!address.isNullOrEmpty()) {
            stgLocation.text = address[0].locality
        }
    }

    private fun getLastLocation() {
        if (checkAccessLocationPermission()) {
            if (checkIsLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location != null) {
                        getCityName(location.latitude, location.longitude)
                    } else {
                        requestNewLocationData()
                    }
                }
            } else {
                showShortToast("Turn on location")
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestAccessLocationPermission()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun requestAccessLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).toTypedArray(),
            PERMISSION_ID
        )
    }

    private fun checkIsLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkAccessLocationPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val PERMISSION_ID = 100

        fun startActivity(context: Context?) {
            context?.startActivity(
                Intent(context, SettingActivity::class.java)
            )
        }
    }

    /* OBSERVERS */
    private fun observeProfileName() {
        viewModel.profileNameResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()

                    profileName = result.data ?: ""
                    accountName.text = profileName
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()

                    handleApiError(result.message) {
                        showAlertDialog(
                            message = "Terjadi kesalahan. Silahkan coba beberapa saat lagi",
                            btnText = "Ok",
                            btnCallback = { dialogInterface ->
                                dialogInterface.dismiss()
                                onBackPressed()
                            }
                        )
                    }
                }
            }
        })
    }

    private fun observeLogoutResult() {
        viewModel.logoutResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                    swipeRefreshLayout.startRefreshing()
                }
                SUCCESS -> {
                    swipeRefreshLayout.stopRefreshing()

                    viewModel.afterLogout()
                    LoginActivity.startActivityClearTask(this)

                    Runnable {
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                    }
                }
                ERROR -> {
                    swipeRefreshLayout.stopRefreshing()
                    handleApiError(result.message) {
                        showAlertDialog(
                            message = "Terjadi kesalahan. Silahkan coba beberapa saat lagi",
                            btnText = "Ok",
                            btnCallback = { dialogInterface ->
                                dialogInterface.dismiss()
                            }
                        )
                    }
                }
            }
        })
    }
}
