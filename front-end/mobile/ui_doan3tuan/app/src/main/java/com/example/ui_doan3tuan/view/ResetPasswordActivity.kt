package com.example.ui_doan3tuan.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.ui_doan3tuan.databinding.ActivityResetPasswordBinding
import com.example.ui_doan3tuan.viewmodel.ApiException
import com.example.ui_doan3tuan.viewmodel.AuthRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val authRepository = AuthRepository()
    private var resetToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy token từ Intent -> lưu trong share
        resetToken = intent.getStringExtra("RESET_TOKEN") ?: ""

        // Validate token
        if (resetToken.isEmpty()) {
            showErrorMessage("Token không hợp lệ. Vui lòng thử lại từ đầu.")
            finish()
            return
        }

        setupViews()
    }

    private fun setupViews() {
        // Setup real-time validation
        binding.etNewPassword.doAfterTextChanged { text ->
            val password = text?.toString() ?: ""
            validatePassword(password)
        }

        binding.etConfirmNewPassword.doAfterTextChanged { text ->
            val confirmPassword = text?.toString() ?: ""
            val newPassword = binding.etNewPassword.text?.toString() ?: ""
            validateConfirmPassword(newPassword, confirmPassword)
        }

        binding.btnResetPasswordSubmit.setOnClickListener {
            handleResetPassword()
        }

        binding.tvBackToLoginReset.setOnClickListener {
            finish()
        }
    }

    private fun handleResetPassword() {
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmNewPassword.text.toString().trim()

        // Validate inputs
        val passwordError = validatePassword(newPassword)
        val confirmPasswordError = validateConfirmPassword(newPassword, confirmPassword)

        if (passwordError != null || confirmPasswordError != null) {
            return
        }

        // Clear errors
        binding.tilNewPassword.error = null
        binding.tilNewPassword.isErrorEnabled = false
        binding.tilConfirmNewPassword.error = null
        binding.tilConfirmNewPassword.isErrorEnabled = false

        // Show loading state
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = authRepository.resetPassword(resetToken, newPassword)

                result.fold(
                    onSuccess = { response ->
                        showLoading(false)
                        showSuccessMessage(response.message)
                        // Navigate back to LoginActivity after a short delay
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }, 1500)
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

    private fun validatePassword(password: String): String? {
        val error = when {
            password.isEmpty() -> "Mật khẩu là bắt buộc"
            password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
            password.length > 32 -> "Mật khẩu không được vượt quá 32 ký tự"
            !isPasswordStrong(password) -> "Mật khẩu phải có chữ hoa, chữ thường, số và ký tự đặc biệt"
            else -> null
        }

        binding.tilNewPassword.error = error
        binding.tilNewPassword.isErrorEnabled = error != null
        return error
    }

    private fun validateConfirmPassword(newPassword: String, confirmPassword: String): String? {
        val error = when {
            confirmPassword.isEmpty() -> "Vui lòng xác nhận mật khẩu"
            newPassword != confirmPassword -> "Mật khẩu xác nhận không khớp"
            else -> null
        }

        binding.tilConfirmNewPassword.error = error
        binding.tilConfirmNewPassword.isErrorEnabled = error != null
        return error
    }

    private fun isPasswordStrong(password: String): Boolean {
        // Backend regex: ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

        return hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnResetPasswordSubmit.text = "ĐANG XỬ LÝ..."
            binding.btnResetPasswordSubmit.isEnabled = false
            binding.btnResetPasswordSubmit.alpha = 0.6f
        } else {
            binding.btnResetPasswordSubmit.text = "Đặt lại mật khẩu"
            binding.btnResetPasswordSubmit.isEnabled = true
            binding.btnResetPasswordSubmit.alpha = 1.0f
        }
    }

    private fun handleError(error: Throwable) {
        val errorMessage = when (error) {
            is ApiException -> {
                when (error.code) {
                    400 -> {
                        when {
                            error.message.contains("token", ignoreCase = true) -> 
                                "Token không hợp lệ hoặc đã hết hạn. Vui lòng thử lại từ đầu."
                            error.message.contains("password", ignoreCase = true) -> 
                                "Mật khẩu không đáp ứng yêu cầu. Vui lòng kiểm tra lại."
                            else -> error.message ?: "Dữ liệu không hợp lệ"
                        }
                    }
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
                handleResetPassword()
            }
            .show()
    }

    private fun showSuccessMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.GREEN)
            .show()
    }
}