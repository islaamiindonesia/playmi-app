package id.islaami.playmi.ui.channel

import android.os.Bundle
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.islaami.playmi.R
import id.islaami.playmi.ui.base.BaseFragment
import kotlinx.android.synthetic.main.channel_about_fragment.*

class ChannelAboutFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.channel_about_fragment, container, false)
    }

    fun setDescription(description: Spanned?) {
        about.text = description
        about.movementMethod = LinkMovementMethod()
    }

    companion object {
        fun newInstance(): Fragment = ChannelAboutFragment()
    }
}
