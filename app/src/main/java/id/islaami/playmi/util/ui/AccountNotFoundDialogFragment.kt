package id.islaami.playmi.util.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import id.islaami.playmi.R
import id.islaami.playmi.util.fromHtmlToSpanned
import kotlinx.android.synthetic.main.account_not_found_dialog_fragment.*
import kotlinx.android.synthetic.main.custom_dialog_fragment.*
import kotlinx.android.synthetic.main.custom_dialog_fragment.btnCancel
import kotlinx.android.synthetic.main.custom_dialog_fragment.btnOk
import java.io.Serializable

class AccountNotFoundDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.account_not_found_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)

        arguments?.let { bundle ->
            email.text = bundle.getString(EXTRA_TEXT).fromHtmlToSpanned()

            btnCancel.apply {
                setOnClickListener {
                    dismiss()
                }
                text = bundle.getString(EXTRA_BTN_CANCEL)
            }

            val okCallback: (() -> Unit)? =
                bundle.getSerializable(EXTRA_OK_CALLBACK) as (() -> Unit)?

            btnOk.apply {
                setOnClickListener {
                    okCallback?.invoke()
                    dismiss()
                }
                text = bundle.getString(EXTRA_BTN_OK)
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
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_BTN_CANCEL = "EXTRA_BTN_CANCEL"
        private const val EXTRA_BTN_OK = "EXTRA_BTN_OK"
        private const val EXTRA_OK_CALLBACK = "EXTRA_OK_CALLBACK"
        private const val EXTRA_OUTSIDE_TOUCH_CALLBACK = "EXTRA_OUTSIDE_TOUCH_CALLBACK"

        private const val TAG_CUSTOM_DIALOG_FRAGMENT = "TAG_CUSTOM_DIALOG_FRAGMENT"

        fun show(
            fragmentManager: FragmentManager?,
            email: String,
            btnCancel: String? = null,
            btnOk: String? = null,
            okCallback: (() -> Unit)? = null,
            outsideTouchCallback: (() -> Unit)? = null
        ) {
            if (fragmentManager != null) {
                AccountNotFoundDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_TEXT, email)
                        if (btnCancel != null) {
                            putString(EXTRA_BTN_CANCEL, btnCancel)
                        }
                        if (btnOk != null) {
                            putString(EXTRA_BTN_OK, btnOk)
                        }
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
