package id.islaami.playmi.ui.intro

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
import id.islaami.playmi.R
import id.islaami.playmi.ui.auth.LoginActivity
import id.islaami.playmi.ui.base.BaseActivity
import id.islaami.playmi.util.Clickable
import id.islaami.playmi.util.ui.showShortToast
import kotlinx.android.synthetic.main.intro_activity.*

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        btnNext.setOnClickListener { LoginActivity.startActivityClearTask(this) }

        introHtmlText.text = createTextAgreement()
        introHtmlText.movementMethod = LinkMovementMethod()
    }

    private fun createTextAgreement(): SpannableString {
        val fulltext = getText(R.string.text_agreement) as SpannedString
        val spannableString = SpannableString(fulltext)
        val annotations = fulltext.getSpans(0, fulltext.length, Annotation::class.java)
        annotations.find {
            it.value == "tnc"
        }.let {
            spannableString.apply {
                setSpan(
                    Clickable(1) { showShortToast("tnc") },
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.layout_background_color
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
            }
        }
        annotations.find {
            it.value == "privacy"
        }.let {
            spannableString.apply {
                setSpan(
                    Clickable(2) { showShortToast("privacy") },
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.accent
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            this@IntroActivity,
                            R.color.layout_background_color
                        )
                    ),
                    fulltext.getSpanStart(it),
                    fulltext.getSpanEnd(it),
                    0
                )
            }
        }

        return spannableString
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, IntroActivity::class.java))
        }
    }
}
