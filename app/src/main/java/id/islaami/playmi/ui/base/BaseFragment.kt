package id.islaami.playmi.ui.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import id.islaami.playmi.R
import id.islaami.playmi.config.injectFeature
import id.islaami.playmi.ui.setting.SettingActivity

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()
    }
}