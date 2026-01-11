package com.example.ui_doan3tuan.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ui_doan3tuan.databinding.ActivityForgotPasswordBinding
import com.example.ui_doan3tuan.viewmodel.ApiException
import com.example.ui_doan3tuan.viewmodel.AuthRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.btnResetPassword.setOnClickListener {
            handleForgotPassword()
        }

        binding.tvBackToLoginForgot.setOnClickListener {
            finish()
        }
    }

    private fun handleForgotPassword() {
        val username = binding.etEmailForgot.text.toString().trim()

        // Validate input
        val validationError = validateUsername(username)
        if (validationError != null) {
            binding.tilEmailForgot.error = validationError
            binding.tilEmailForgot.isErrorEnabled = true
            return
        }

        // Clear previous errors
        binding.tilEmailForgot.error = null
        binding.tilEmailForgot.isErrorEnabled = false

        // Show loading state
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = authRepository.forgotPassword(username)

                result.fold(
                    onSuccess = { response ->
                        showLoading(false)
                        // Success: Navigate to ResetPasswordActivity with token
                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("RESET_TOKEN", response.token)
                        startActivity(intent)
                        // Optionally finish this activity
                        // finish()
                    },
                    onFailure = { error ->
                        showLoading(false)
                        handleError(error)
                    }
                )
            } catch (e: Exception) {
                showLoading(false)
                showErrorMessage("Đã xảy ra lỗi không mong muốn: ${e.message}")
            }
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isEmpty() -> "Email/SĐT là bắt buộc"
            username.contains("@") -> {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    null
                } else {
                    "Email không hợp lệ"
                }
            }
            username.matches(Regex("\\d+")) -> {
                if (username.length in 10..11) {
                    null
                } else {
                    "SĐT phải có 10-11 số"
                }
            }
            else -> "Email hoặc SĐT không hợp lệ"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnResetPassword.text = "ĐANG XỬ LÝ..."
            binding.btnResetPassword.isEnabled = false
            binding.btnResetPassword.alpha = 0.6f
        } else {
            binding.btnResetPassword.text = "Gửi yêu cầu"
            binding.btnResetPassword.isEnabled = true
            binding.btnResetPassword.alpha = 1.0f
        }
    }

    private fun handleError(error: Throwable) {
        val errorMessage = when (error) {
            is ApiException -> {
                when (error.code) {
                    400 -> "Vui lòng nhập đầy đủ thông tin"
                    404 -> "Không tìm thấy tài khoản với email/SĐT này"
                    500 -> "Lỗi server, vui lòng thử lại sau"
                    else -> error.message ?: "Đã xảy ra lỗi"
                }
            }
            else -> "Lỗi kết nối: ${error.message}"
        }
        showErrorMessage(errorMessage)
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.RED)
            .setAction("THỬ LẠI") {
                handleForgotPassword()
            }
            .show()
    }
}