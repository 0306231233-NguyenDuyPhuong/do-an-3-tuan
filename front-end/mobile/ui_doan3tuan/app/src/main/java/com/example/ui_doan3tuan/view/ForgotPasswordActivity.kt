
package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var btnResetPassword: Button
    private lateinit var tvBackToLoginForgot: TextView

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
    private fun initViews(){
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvBackToLoginForgot = findViewById(R.id.tvBackToLoginForgot)

    }
    private fun setupListener(){
        btnResetPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
        tvBackToLoginForgot.setOnClickListener {
            finish()
        }
    }
}
