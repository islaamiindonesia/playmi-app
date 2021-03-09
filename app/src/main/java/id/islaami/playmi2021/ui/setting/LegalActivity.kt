package id.islaami.playmi2021.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import id.islaami.playmi2021.R
import id.islaami.playmi2021.ui.base.BaseSpecialActivity
import id.islaami.playmi2021.util.ui.setupToolbar
import kotlinx.android.synthetic.main.legal_activity.*

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
