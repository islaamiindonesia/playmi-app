package id.islaami.playmi.ui.setting.help

import android.content.Context
import android.content.Intent
import android.os.Bundle
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.ui.setupToolbar
import kotlinx.android.synthetic.main.web_help_activity.*

class WebHelpActivity : BaseActivity() {
    var webUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_help_activity)

        setupToolbar(toolbar)

        webUrl = intent.getStringExtra(URL) ?: ""
        webView.loadUrl(webUrl)
    }

    companion object {
        const val URL = "URL"

        fun startActivity(context: Context, url: String) {
            context.startActivity(Intent(context, WebHelpActivity::class.java).apply {
                putExtra(URL, url)
            })
        }
    }
}
