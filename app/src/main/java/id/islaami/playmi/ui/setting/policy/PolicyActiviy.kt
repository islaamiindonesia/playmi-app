package id.islaami.playmi.ui.setting.policy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.data.model.setting.Policy
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.setting.SettingViewModel
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.fromHtmlToSpanned
import id.islaami.playmi.util.ui.setupToolbar
import id.islaami.playmi.util.ui.showSnackbar
import kotlinx.android.synthetic.main.policy_activiy.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PolicyActiviy : BaseActivity() {
    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.policy_activiy)

        setupToolbar(toolbar)

        viewModel.initPolicyActivity()

        when (intent.getStringExtra(TYPE)) {
            "ABOUT_PLAYMI" -> {
                viewModel.aboutApp()
                supportActionBar?.title = "Tentang Aplikasi"
            }
            "COOP_PLAYMI" -> {
                viewModel.cooperation()
                supportActionBar?.title = "Kerjasama"
            }
            "TNC_PLAYMI" -> {
                viewModel.userTNC()
                supportActionBar?.title = "Ketentuan Pengguna"
            }
            "PRIVACY_PLAYMI" -> {
                viewModel.privacyPolicy()
                supportActionBar?.title = "Kebijakan Privasi"
            }
        }

        observePolicy()
    }

    companion object {
        const val TYPE = "TYPE"

        fun startActivity(context: Context?, type: String) {
            context?.startActivity(
                Intent(context, PolicyActiviy::class.java).apply {
                    putExtra(TYPE, type)
                }
            )
        }
    }

    private fun observePolicy() {
        viewModel.policyResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    val policy = result.data ?: Policy()

                    content.text = policy.content.fromHtmlToSpanned()
                }
                ERROR -> {
                    showSnackbar(getString(R.string.error_message_default))
                }
            }
        })
    }
}
