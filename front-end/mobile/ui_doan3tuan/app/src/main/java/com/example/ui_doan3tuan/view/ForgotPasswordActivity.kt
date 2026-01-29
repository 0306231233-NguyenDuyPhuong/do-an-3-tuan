package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.ErrorResponse
import com.example.ui_doan3tuan.model.ForgotPasswordRequest
import com.example.ui_doan3tuan.model.ForgotPasswordResponse
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var btnResetPassword: Button
    private lateinit var tvBackToLoginForgot: TextView
    private lateinit var tilEmailForgot: TextInputLayout
    private lateinit var etEmailForgot: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        setupListener()
    }

    private fun initViews() {
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvBackToLoginForgot = findViewById(R.id.tvBackToLoginForgot)
        tilEmailForgot = findViewById(R.id.tilEmailForgot)
        etEmailForgot = findViewById(R.id.etEmailForgot)
    }

    private fun setupListener() {
        btnResetPassword.setOnClickListener { guiYeuCauResetPassword() }
        tvBackToLoginForgot.setOnClickListener { finish() }
    }

    private fun guiYeuCauResetPassword() {
        val email = etEmailForgot.text.toString().trim()
        tilEmailForgot.error = null

        if (email.isEmpty()) {
            tilEmailForgot.error = "Vui lòng nhập email"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailForgot.error = "Email không hợp lệ"
            return
        }

        btnResetPassword.isEnabled = false
        btnResetPassword.text = "Đang gửi..."

        val request = ForgotPasswordRequest(username = email)
        ApiClient.apiService.forgotPassword(request)
            .enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Gửi yêu cầu"

                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        val msg = response.errorBody()?.string()?.let { body ->
                            try {
                                Gson().fromJson(body, ErrorResponse::class.java).message
                            } catch (_: Exception) { null }
                        } ?: "Có lỗi xảy ra"
                        Toast.makeText(this@ForgotPasswordActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(
                    call: Call<ForgotPasswordResponse>,
                    t: Throwable
                ) {
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Gửi yêu cầu"
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Lỗi mạng: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
