package id.islaami.playmi.ui.setting.recommendation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.setting.SettingViewModel
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.setTextChangedListener
import id.islaami.playmi.util.ui.setupToolbar
import id.islaami.playmi.util.ui.setVisibilityToGone
import id.islaami.playmi.util.ui.setVisibilityToVisible
import id.islaami.playmi.util.ui.showAlertDialog
import id.islaami.playmi.util.ui.showSnackbar
import kotlinx.android.synthetic.main.recommendation_activity.*
import kotlinx.android.synthetic.main.recommendation_activity.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecommendationActivity : BaseActivity() {
    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recommendation_activity)

        setupToolbar(toolbar)

        viewModel.initRecommendationActivity()
        observeAddRecommendation()
        observeFormValidation()

        btnSend.setOnClickListener {
            viewModel.addRecommendation(
                channelName = channelName.text.toString(),
                channelUrl = channelUrl.text.toString()
            )
        }

        setupForm()

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun setupForm() {
        channelName.setTextChangedListener(
            layoutChannelName,
            errorMessage = "Nama kanal tidak boleh kosong"
        ) {
            viewModel.channelName = it
            viewModel.updateFormFilled()
        }

        channelUrl.setTextChangedListener(
            layoutChannelUrl,
            errorMessage = "Tautan tidak boleh kosong"
        ) {
            viewModel.channelUrl = it
            viewModel.updateFormFilled()
        }
    }

    private fun observeFormValidation() {
        viewModel.formFilled.observe(this, Observer { result ->
            btnSend.isEnabled = result
        })
    }

    private fun observeAddRecommendation() {
        viewModel.recommendationResultLd.observe(this, Observer { result ->
            when (result.status) {
                LOADING -> {
                    progressBar.setVisibilityToVisible()
                    btnSend.setVisibilityToGone()
                }
                SUCCESS -> {
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    showAlertDialog(
                        message = "Terima kasih atas rekomendasi Anda.",
                        btnText = "OK",
                        btnCallback = { dialog ->
                            dialog.dismiss()
                            onBackPressed()
                        }
                    )
                }
                ERROR -> {
                    btnSend.setVisibilityToVisible()
                    progressBar.setVisibilityToGone()

                    handleApiError(result.message) { showSnackbar(it) }
                }
            }
        })
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, RecommendationActivity::class.java))
        }
    }
}
