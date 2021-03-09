package id.islaami.playmi2021.ui.intro

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import id.islaami.playmi2021.R
import id.islaami.playmi2021.data.model.kotpref.Default
import id.islaami.playmi2021.ui.auth.LoginActivity
import id.islaami.playmi2021.ui.base.BaseActivity
import id.islaami.playmi2021.ui.setting.LegalActivity
import id.islaami.playmi2021.util.Clickable
import kotlinx.android.synthetic.main.intro_activity.*

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        btnNext.setOnClickListener {
            LoginActivity.startActivityClearTask(this)
            Default.hasSeenIntro = true
        }

        introHtmlText.text = createTextAgreement()
        introHtmlText.movementMethod = LinkMovementMethod()
    }

    private fun createTextAgreement(): SpannableString {
        val fulltext = getText(R.string.text_agreement) as SpannedString
        val spannableString = SpannableString(fulltext)
        val annotations = fulltext.getSpans(0, fulltext.length, Annotation::class.java)
        annotations.find {
            it.value == "tnc"
        }.let { annotation ->
            spannableString.apply {
                setSpan(
                    Clickable(1) {
                        LegalActivity.startActivity(this@IntroActivity, "TNC_PLAYMI")
                    },
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.layout_background_color
                        )
                    ),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
            }
        }
        annotations.find {
            it.value == "privacy"
        }.let { annotation ->
            spannableString.apply {
                setSpan(
                    Clickable(2) {
                        LegalActivity.startActivity(this@IntroActivity, "PRIVACY_PLAYMI")
                    },
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.layout_background_color
                        )
                    ),
                    fulltext.getSpanStart(annotation),
                    fulltext.getSpanEnd(annotation),
                    0
                )
            }
        }

        return spannableString
    }

    companion object {
        fun startActivityClearTask(context: Context?) {
            context?.startActivity(
                Intent(context, IntroActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
