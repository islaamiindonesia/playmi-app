package id.islaami.playmi.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import id.islaami.playmi.R
import id.islaami.playmi.data.model.setting.LegalityContent
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.ui.base.BaseSpecialActivity
import id.islaami.playmi.util.ResourceStatus.*
import id.islaami.playmi.util.handleApiError
import id.islaami.playmi.util.ui.setupToolbar
import id.islaami.playmi.util.ui.showAlertDialog
import id.islaami.playmi.util.ui.showSnackbar
import kotlinx.android.synthetic.main.legal_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegalActivity : BaseSpecialActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.legal_activity)

        setupToolbar(toolbar)

        when (intent.getStringExtra(TYPE)) {
            "ABOUT_PLAYMI" -> {
                supportActionBar?.title = "Tentang Aplikasi"
                content.loadUrl("https://islaami.id/about")
            }
            "COOP_PLAYMI" -> {
                supportActionBar?.title = "Kerjasama"
                content.loadUrl("https://islaami.id/cooperation")
            }
            "TNC_PLAYMI" -> {
                supportActionBar?.title = "Ketentuan Pengguna"
                content.loadUrl("https://islaami.id/terms-and-condition")
            }
            "PRIVACY_PLAYMI" -> {
                supportActionBar?.title = "Kebijakan Privasi"
                content.loadUrl("https://islaami.id/privacy-policy")
            }
            else -> {
                supportActionBar?.title = "Bantuan"
                content.loadUrl("https://www.google.com")
            }
        }
    }

    companion object {
        const val TYPE = "TYPE"

        fun startActivity(context: Context?, type: String? = null) {
            context?.startActivity(
                Intent(context, LegalActivity::class.java).apply {
                    putExtra(TYPE, type)
                }
            )
        }
    }
}
