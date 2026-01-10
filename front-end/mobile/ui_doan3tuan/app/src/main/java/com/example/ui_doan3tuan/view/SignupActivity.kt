package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.RegisterResponse
import com.example.ui_doan3tuan.viewmodel.RegisterViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var edtHoTen: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtMatKhau: EditText
    private lateinit var edtNhapLaiMatKhau: EditText

    private lateinit var btnDangKy: Button
    private lateinit var txtQuayLai: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        timView()
        xuLySuKien()
        xemKetQua()
    }

    private fun timView() {
        edtHoTen = findViewById(R.id.etName)
        edtEmail = findViewById(R.id.etEmailSignup)
        edtMatKhau = findViewById(R.id.etPasswordSignup)
        edtNhapLaiMatKhau = findViewById(R.id.etConfirmPassword)
        btnDangKy = findViewById(R.id.btnSignup)
        txtQuayLai = findViewById(R.id.tvBackToLogin)
    }

    private fun xuLySuKien() {
        btnDangKy.setOnClickListener {
            dangKy()
        }

        txtQuayLai.setOnClickListener {
            
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun dangKy() {
        val hoTen = edtHoTen.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val matKhau = edtMatKhau.text.toString()
        val nhapLaiMatKhau = edtNhapLaiMatKhau.text.toString()
        
        if (hoTen.isEmpty()) {
            edtHoTen.error = "Nhập họ tên"
            return
        }

        if (email.isEmpty()) {
            edtEmail.error = "Nhập email hoặc số điện thoại"
            return
        }

        if (matKhau.isEmpty()) {
            edtMatKhau.error = "Nhập mật khẩu"
            return
        }

        if (nhapLaiMatKhau.isEmpty()) {
            edtNhapLaiMatKhau.error = "Nhập lại mật khẩu"
            return
        }
        
        if (matKhau.length < 8) {
            edtMatKhau.error = "Mật khẩu ít nhất 8 ký tự"
            return
        }
        
        if (matKhau != nhapLaiMatKhau) {
            edtNhapLaiMatKhau.error = "Mật khẩu không khớp"
            return
        }
        
        viewModel.register(email, matKhau, nhapLaiMatKhau, hoTen)


        btnDangKy.text = "Đang xử lý..."
        btnDangKy.isEnabled = false
    }

    private fun xemKetQua() {
        // Xem khi đang loading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading == false) {
                btnDangKy.text = "Đăng ký"
                btnDangKy.isEnabled = true
            }
        }

        // Xem khi đăng ký thành công
        viewModel.registerSuccess.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show()

                hienThiThongBaoThanhCong(response)
            }
        }
        
        viewModel.errorMessage.observe(this) { error ->
            if (error != null && error.isNotEmpty()) {
                Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun hienThiThongBaoThanhCong(response: RegisterResponse) {

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(" Thành công!")
            .setMessage("Tạo tài khoản thành công cho: ${response.user.full_name}")
            .setPositiveButton("Đăng nhập ngay") { dialog, _ ->
                dialog.dismiss()


                val intent = Intent(this, LoginActivity::class.java)
                val emailDeDien = response.user.email ?: ""
                if (emailDeDien.isNotEmpty()) {
                    intent.putExtra("email", emailDeDien)
                }
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
}