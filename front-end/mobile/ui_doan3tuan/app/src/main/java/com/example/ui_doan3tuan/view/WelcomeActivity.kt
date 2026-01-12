package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.viewmodel.LoginViewModel

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnCreateAccount: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Log.d("WelcomeActivity", "Màn hình chào đang chạy")

        // Tạo ViewModel để kiểm tra
        val loginViewModel = LoginViewModel()
        loginViewModel.init(applicationContext)

        // Tìm các view
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvLogin = findViewById(R.id.tvLogin)

        // Kiểm tra đăng nhập tự động trước
        checkAutoLogin(loginViewModel)

        // Xử lý click
        setupClickListeners()
    }

    private fun checkAutoLogin(loginViewModel: LoginViewModel) {
        // Kiểm tra xem đã đăng nhập chưa
        if (loginViewModel.isLoggedIn()) {
            Log.d("WelcomeActivity", "Đã đăng nhập trước đó")

            // Kiểm tra token còn hiệu lực không
            if (loginViewModel.isTokenValid()) {
                Log.d("WelcomeActivity", "Token còn hiệu lực, tự động vào News")

                // Vào thẳng màn hình Newsletter
                val intent = Intent(this, NewsletterActivity::class.java)
                startActivity(intent)
                finish()
                return
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.d("WelcomeActivity", "Chưa đăng nhập lần nào")
        }
    }

    private fun setupClickListeners() {
        // Nút Tạo tài khoản -> Chuyển sang SignupActivity
        btnCreateAccount.setOnClickListener {
            Log.d("WelcomeActivity", "Click Tạo tài khoản")
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Text Đăng nhập -> Chuyển sang LoginActivity
        tvLogin.setOnClickListener {
            Log.d("WelcomeActivity", "Click Đăng nhập")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}