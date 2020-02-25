package com.example.playmi.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.playmi.R
import com.example.playmi.ui.auth.LoginActivity
import com.example.playmi.ui.base.BaseActivity
import com.example.playmi.util.ResourceStatus.*
import kotlinx.android.synthetic.main.setting_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : BaseActivity() {

    private val viewModel: SettingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity)

        viewModel.initSettingActivity()
        observeLogoutResult()

        // temporary
        logout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeLogoutResult() {
        viewModel.logoutResultLd.observe(this, Observer { result ->
            when (result?.status) {
                LOADING -> {
                }
                SUCCESS -> {
                    LoginActivity.startActivityAfterLogout(this)
                }
                ERROR -> {
                }
            }
        })
    }

    companion object {
        fun startActivity(context: Context?) {
            context?.startActivity(
                Intent(context, SettingActivity::class.java)
            )
        }
    }
}
