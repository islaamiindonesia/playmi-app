package id.islaami.playmi.ui.setting.recommendation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseSpecialActivity
import id.islaami.playmi.ui.setting.SettingViewModel
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.recommendation_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecommendationActivity : BaseSpecialActivity() {
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
        channelName.addTextChangedListener {
            viewModel.channelName = it.toString()
            viewModel.updateFormFilled()
        }

        channelUrl.addTextChangedListener {
            viewModel.channelUrl = it.toString()
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

                    ReportDialogFragment.show(
                        fragmentManager = supportFragmentManager,
                        title = "Terima kasih atas rekomendasi Anda untuk membantu Playmi menjadi lebih baik.",
                        text = "Kami tidak dapat melihat dan menanggapi setiap rekomendasi, namun beberapa rekomendasi membantu kami meningkatkan layanan untuk semua orang.",
                        okCallback = {
                            channelName.setText("")
                            channelUrl.setText("")
                        },
                        outsideTouchCallback = {
                            channelName.setText("")
                            channelUrl.setText("")
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
