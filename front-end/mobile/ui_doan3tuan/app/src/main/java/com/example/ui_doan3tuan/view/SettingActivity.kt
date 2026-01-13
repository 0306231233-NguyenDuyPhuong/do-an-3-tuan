package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.viewmodel.LogoutViewModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import kotlin.getValue

class SettingActivity : AppCompatActivity() {
    private val viewModel: LogoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.imgThoatCaiDat).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnDangXuat).setOnClickListener {
            val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val accessToken = sharedPref.getString("access_token", "")
            val refreshToken = sharedPref.getString("refresh_token", "")
            sharedPref.edit().clear().apply()
            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                viewModel.logout("Bearer $accessToken", refreshToken)
            }

        }
        viewModel.logout.observe(this) { isSuccess ->
            if (isSuccess) {
                val logout = Intent(this, LoginActivity::class.java)
                logout.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logout)
                finish()
            }
        }
        findViewById<Button>(R.id.btnDangXuat).setOnClickListener {
            val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val refreshToken = sharedPref.getString("refresh_token", null)
            if (refreshToken != null) {
                viewModel.logout(token,refreshToken)
                sharedPref.edit().remove("access_token").apply()
                sharedPref.edit().remove("refresh_token").apply()
            }

        }


    }
}