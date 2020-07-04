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
import id.islaami.playmi.ui.setting.policy.PolicyActiviy
import id.islaami.playmi.util.Clickable
import kotlinx.android.synthetic.main.intro_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroActivity : BaseActivity() {
    private val viewModel: IntroViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        btnNext.setOnClickListener {
            LoginActivity.startActivityClearTask(this)
            viewModel.hasSeenIntro = true
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
                        PolicyActiviy.startActivity(this@IntroActivity, "TNC_PLAYMI")
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
                        PolicyActiviy.startActivity(this@IntroActivity, "PRIVACY_PLAYMI")
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
