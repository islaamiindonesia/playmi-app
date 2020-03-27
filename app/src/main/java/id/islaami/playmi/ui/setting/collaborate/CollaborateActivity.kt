package id.islaami.playmi.ui.setting.collaborate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.createClickableString
import id.islaami.playmi.util.ui.setupToolbar
import kotlinx.android.synthetic.main.collaborate_activity.*

class CollaborateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collaborate_activity)

        setupToolbar(toolbar)

        fullText.apply {
            text = createClickableString(
                context = this@CollaborateActivity,
                key = "email",
                backgroundColor = R.color.white,
                foregroundColor = R.color.accent,
                isUnderLine = false,
                stringRes = R.string.message_collaborate,
                onClickAction = {
                    val mailTo = "mailto:youdant@gmail.com"
                    startActivity(Intent(Intent.ACTION_SENDTO).setData(Uri.parse(mailTo)))
                }
            )
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, CollaborateActivity::class.java))
        }
    }
}
