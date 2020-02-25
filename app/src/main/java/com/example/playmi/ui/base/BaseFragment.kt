package com.example.playmi.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.playmi.config.injectFeature

abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        injectFeature()
    }
}