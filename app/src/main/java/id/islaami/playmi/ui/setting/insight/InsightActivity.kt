package id.islaami.playmi.ui.setting.insight

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseSpecialActivity
import id.islaami.playmi.ui.setting.SettingViewModel
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.*
import kotlinx.android.synthetic.main.insight_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InsightActivity : BaseSpecialActivity() {
    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.insight_activity)

        setupToolbar(toolbar)

        viewModel.initInsightActivity()
        observeAddInsight()

        btnSend.setOnClickListener {
            viewModel.addInsight(userInsight.text.toString())
        }

        userInsight.addTextChangedListener {
            btnSend.isEnabled = it.toString().isNotEmpty()
        }
    }

    private fun observeAddInsight() {
        viewModel.insightResultLd.observe(this, Observer { result ->
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
                        title = "Terima kasih atas saran/masukkan Anda untuk membantu Playmi menjadi lebih baik.",
                        text = "Kami tidak dapat melihat dan menanggapi setiap masukan, namun eberapa masukan membantu kami meningkatkan layanan untuk semua orang.",
                        okCallback = { userInsight.setText("") },
                        outsideTouchCallback = { userInsight.setText("") }
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
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, InsightActivity::class.java))
        }
    }
}
