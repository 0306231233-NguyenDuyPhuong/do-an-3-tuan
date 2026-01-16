package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.session.SessionManager

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnCreateAccount: Button
    private lateinit var tvLogin: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Log.d("WelcomeActivity", "Màn hình chào đang chạy")

        sessionManager = SessionManager(applicationContext)

        bindViews()
        checkAutoLogin()

        setupClickListeners()
    }

    private fun bindViews() {
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun checkAutoLogin() {
        if (sessionManager.isLoggedIn()) {
            Log.d("WelcomeActivity", "Đã đăng nhập → vào Newsletter")

            startActivity(Intent(this, NewsletterActivity::class.java))
            finish()
        } else {
            Log.d("WelcomeActivity", "Chưa đăng nhập")
        }
    }

    private fun setupClickListeners() {
        btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
