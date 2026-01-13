package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.User
import com.example.ui_doan3tuan.viewmodel.LoginViewModel
class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
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

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.init(applicationContext)

        edtUsername = findViewById(R.id.etEmail)
        edtPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle)

        if (kiemTraDaDangNhap()) {
            chuyenManHinhChinh()
            return
        }

        setupEvents()
    }

    private fun setupEvents() {

        btnLogin.setOnClickListener {
            xuLyDangNhap()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // Quên mật khẩu
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Ẩn/hiện mật khẩu
        ivPasswordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
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

        // Hiển thị loading
        btnLogin.text = "Đang đăng nhập..."
        btnLogin.isEnabled = false

        viewModel.login(username, password,
            onSuccess = { accessToken, refreshToken, userData ->
                xuLyDangNhapThanhCong(accessToken, userData)
            },
            onError = { error ->
                xuLyDangNhapThatBai(error)
            }
        )
    }

    private fun xuLyDangNhapThanhCong(accessToken: String, userData: User) {
        luuThongTinUser(userData)
        Toast.makeText(this, "Đăng nhập thành công! ", Toast.LENGTH_SHORT).show()
        btnLogin.text = "Đăng nhập"
        btnLogin.isEnabled = true

        chuyenManHinhChinh()
    }

    private fun xuLyDangNhapThatBai(error: String) {
        btnLogin.text = "Đăng nhập"
        btnLogin.isEnabled = true

        Toast.makeText(this, error, Toast.LENGTH_LONG).show()

        if (error.contains("Sai thông tin")) {
            edtPassword.text.clear()
            edtPassword.requestFocus()
        }
    }

    private fun kiemTraDaDangNhap(): Boolean {
        return viewModel.isLoggedIn()
    }

    private fun chuyenManHinhChinh() {
        val intent = Intent(this, NewsletterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun luuThongTinUser(userData: User) {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", userData.id)
            putString("user_name", userData.full_name)
            putString("user_email", userData.email ?: "")
            putString("user_phone", userData.phone ?: "")
            apply()
        }


    }
}