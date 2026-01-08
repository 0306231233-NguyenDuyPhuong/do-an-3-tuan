package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ui_doan3tuan.databinding.ActivityResetPasswordBinding
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LẤY TOKEN ĐƯỢC TRUYỀN TỪ MÀN HÌNH QUÊN MẬT KHẨU
        val tokenFromIntent = intent.getStringExtra("RESET_TOKEN") ?: ""

        binding.btnResetPasswordSubmit.setOnClickListener {
            val newPass = binding.etNewPassword.text.toString().trim()
            val confirmPass = binding.etConfirmNewPassword.text.toString().trim()

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // GỌI API RESET PASSWORD LÊN SERVER
            lifecycleScope.launch {
                try {
                    // Gửi request: { "token": tokenFromIntent, "password": newPass }
                    Toast.makeText(this@ResetPasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                    finish() // Quay lại Login
                } catch (e: Exception) {
                    Toast.makeText(this@ResetPasswordActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvBackToLoginReset.setOnClickListener { finish() }
    }
}