package com.example.ui_doan3tuan.view

import android.graphics.Color
import android.os.Bundle
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
                        // Success: Hiển thị thông báo và quay về LoginActivity
                        showSuccessMessage(response.message ?: "Mật khẩu mới đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.")
                        // Tự động quay về LoginActivity sau 2 giây
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 2000)
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
            username.isEmpty() -> "Email là bắt buộc"
            username.contains("@") -> {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    null
                } else {
                    "Email không hợp lệ"
                }
            }
            else -> "Vui lòng nhập email để nhận mật khẩu mới"
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
                    400 -> error.message ?: "Vui lòng nhập email hợp lệ"
                    404 -> error.message ?: "Không tìm thấy tài khoản với email này"
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

    private fun showSuccessMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.GREEN)
            .show()
    }
}