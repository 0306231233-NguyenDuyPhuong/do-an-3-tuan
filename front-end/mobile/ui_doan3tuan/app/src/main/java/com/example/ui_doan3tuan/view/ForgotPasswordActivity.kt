package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ui_doan3tuan.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener {
            val username = binding.etEmailForgot.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email hoặc Số điện thoại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Bước 1: Gọi API forgotPassword (Sử dụng Retrofit của bạn)
                    // val response = apiService.forgotPassword(ForgotRequest(username))

                    // Giả lập nhận Token từ Backend thành công:
                    val serverToken = "MÃ_TOKEN_TỪ_SERVER"

                    // Bước 2: Chuyển sang màn hình ResetPassword và gửi kèm Token
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("RESET_TOKEN", serverToken)
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@ForgotPasswordActivity, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvBackToLoginForgot.setOnClickListener { finish() }
    }
}