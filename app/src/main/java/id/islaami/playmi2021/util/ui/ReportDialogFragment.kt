package id.islaami.playmi2021.util.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import id.islaami.playmi2021.R
import kotlinx.android.synthetic.main.report_dialog_fragment.*
import java.io.Serializable

class ReportDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.report_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)

        arguments?.let { bundle ->
            val titleText = bundle.getString(EXTRA_TITLE)
            if (titleText != null) title.apply {
                setVisibilityToVisible()
                text = titleText
                message.textSize = 18f
            }
            message.text = bundle.getString(EXTRA_TEXT)

            val okCallback: (() -> Unit)? =
                bundle.getSerializable(EXTRA_OK_CALLBACK) as (() -> Unit)?

            btnClose.apply {
                setOnClickListener {
                    okCallback?.invoke()
                    dismiss()
                }
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        arguments?.let { bundle ->
            val outsideTouchCallback: (() -> Unit)? =
                bundle.getSerializable(EXTRA_OUTSIDE_TOUCH_CALLBACK) as (() -> Unit)?
            outsideTouchCallback?.invoke()
            dismiss()
        } ?: run {
            super.onCancel(dialog)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_OK_CALLBACK = "EXTRA_OK_CALLBACK"
        private const val EXTRA_OUTSIDE_TOUCH_CALLBACK = "EXTRA_OUTSIDE_TOUCH_CALLBACK"

        private const val TAG_CUSTOM_DIALOG_FRAGMENT = "TAG_CUSTOM_DIALOG_FRAGMENT"

        fun show(
            fragmentManager: FragmentManager?,
            title: String? = null,
            text: String,
            okCallback: (() -> Unit)? = null,
            outsideTouchCallback: (() -> Unit)? = null
        ) {
            if (fragmentManager != null) {
                ReportDialogFragment().apply {
                    arguments = Bundle().apply {
                        if (title != null) {
                            putString(EXTRA_TITLE, title)
                        }
                        putString(EXTRA_TEXT, text)
                        if (okCallback != null) putSerializable(
                            EXTRA_OK_CALLBACK,
                            okCallback as Serializable
                        )
                        if (outsideTouchCallback != null) putSerializable(
                            EXTRA_OUTSIDE_TOUCH_CALLBACK,
                            outsideTouchCallback as Serializable
                        )
                    }
                }.show(fragmentManager, TAG_CUSTOM_DIALOG_FRAGMENT)
            }
        }
    }
}
