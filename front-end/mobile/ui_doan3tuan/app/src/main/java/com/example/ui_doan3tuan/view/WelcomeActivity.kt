package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_doan3tuan.R

class WelcomeActivity : AppCompatActivity() {

    // Khai báo biến views
    private lateinit var btnCreateAccount: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        findViews()

        setupClickListeners()
    }


    private fun findViews() {

        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvLogin = findViewById(R.id.tvLogin)


    }


    private fun setupClickListeners() {
        // Nút Tạo tài khoản -> Chuyển sang SignupActivity
        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Text Đăng nhập -> Chuyển sang LoginActivity
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}