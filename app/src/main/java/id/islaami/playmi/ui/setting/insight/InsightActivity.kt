package id.islaami.playmi.ui.setting.insight

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
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
import kotlinx.android.synthetic.main.insight_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InsightActivity : BaseActivity() {
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

        userInsight.setTextChangedListener(
            layoutInsight,
            errorMessage = "Anda belum mengisi saran/masukan"
        ) {
            btnSend.isEnabled = it.isNotEmpty()
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

                    showAlertDialog(
                        message = "Terima kasih atas masukkan Anda.",
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
        fun startActivity(context: Context?) {
            context?.startActivity(Intent(context, InsightActivity::class.java))
        }
    }
}
