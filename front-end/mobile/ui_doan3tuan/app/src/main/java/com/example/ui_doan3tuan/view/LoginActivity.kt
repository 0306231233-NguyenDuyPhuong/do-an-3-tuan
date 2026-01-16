package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var ivPasswordToggle: ImageView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(applicationContext)

        bindViews()

        if (sessionManager.isLoggedIn()) {
            chuyenManHinhChinh()
            return
        }

        setupEvents()
    }

    private fun bindViews() {
        edtUsername = findViewById(R.id.etEmail)
        edtPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle)
    }

    private fun setupEvents() {

        btnLogin.setOnClickListener {
            xuLyDangNhap()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        ivPasswordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            edtPassword.inputType =
                if (isPasswordVisible)
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                else
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            edtPassword.setSelection(edtPassword.text.length)
        }
    }

    private fun xuLyDangNhap() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString()

        if (username.isEmpty()) {
            edtUsername.error = "Vui lòng nhập email hoặc số điện thoại"
            return
        }

        if (password.isEmpty()) {
            edtPassword.error = "Vui lòng nhập mật khẩu"
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = "Đang đăng nhập..."

        viewModel.login(
            username = username,
            password = password,
            onSuccess = { response ->

                sessionManager.saveSession(
                    accessToken = response.accessToken!!,
                    refreshToken = response.refreshToken!!,
                    user = response.user
                )

                btnLogin.isEnabled = true
                btnLogin.text = "Đăng nhập"

                chuyenManHinhChinh()
            },
            onError = { error ->
                btnLogin.isEnabled = true
                btnLogin.text = "Đăng nhập"
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                edtPassword.text.clear()
            }
        )
    }

    private fun chuyenManHinhChinh() {
        startActivity(Intent(this, NewsletterActivity::class.java))
        finish()
    }
}