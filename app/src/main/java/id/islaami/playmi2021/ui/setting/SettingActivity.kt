package id.islaami.playmi2021.ui.setting
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.firebase.iid.FirebaseInstanceId
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.kotpref.Mode
import id.islaami.playmi2021.ui.auth.LoginActivity
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.ui.setting.insight.InsightActivity
import id.islaami.playmi2021.ui.setting.profile.ProfileActivity
import id.islaami.playmi2021.ui.setting.recommendation.RecommendationActivity
import id.islaami.playmi2021.ui.setting.report.ReportActivity
import id.islaami.playmi2021.util.ResourceStatus.*
import id.islaami.playmi2021.util.handleApiError
import id.islaami.playmi2021.util.ui.*
import kotlinx.android.synthetic.main.setting_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SettingActivity : BaseSpecialActivity() {
    private val viewModel: SettingViewModel by viewModel()

    private var profileName = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)

        setupToolbar(toolbar)

        viewModel.initSettingActivity()
        if (viewModel.getProfile() != null) {
            accountName.text = viewModel.getProfile()?.fullname
        } else {
            observeProfileName()
        }
        observeLogoutResult()

        val packageInfo = this.packageManager?.getPackageInfo(packageName, 0)
        if (packageInfo != null) {
            version.text = getString(R.string.app_version, packageInfo.versionName)
        }

        stgLanguage.text = Locale(viewModel.selectedLocale).displayLanguage

        setupButton()

        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.accent)
            setProgressBackgroundColorSchemeResource(R.color.refresh_icon_background)
            setOnRefreshListener { viewModel.getProfileName() }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    private fun setupButton() {
        toggleTheme.apply {
            this.isChecked = Mode.appMode == MODE_NIGHT_YES
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Mode.appMode = MODE_NIGHT_YES
                } else {
                    Mode.appMode = MODE_NIGHT_NO
                }
                setDefaultNightMode(Mode.appMode)
            }
        }

        btnAccount.setOnClickListener()
        {
//            showLongToast("open islaami")
            ProfileActivity.startActivity(this)
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
            startActivity(
                    Intent.createChooser(Intent().apply {
                        action = Intent.ACTION_VIEW
                        setData(Uri.parse("https://support.allislaam.com/"))
                    }, "Buka menggunakan:")
            )
        }

        btnCollaborate.setOnClickListener()
        {
            LegalActivity.startActivity(this, "COOP_PLAYMI")
        }

        btnAbout.setOnClickListener()
        {
            LegalActivity.startActivity(this, "ABOUT_PLAYMI")
        }

        btnTNC.setOnClickListener()
        {
            LegalActivity.startActivity(this, "TNC_PLAYMI")
        }

        btnPrivacy.setOnClickListener()
        {
            LegalActivity.startActivity(this, "PRIVACY_PLAYMI")
        }

        btnLogout.setOnClickListener()
        {
            viewModel.logout()
        }
    }


    companion object {

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
