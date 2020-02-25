package com.example.playmi.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playmi.config.injectFeature

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()
    }
}