package com.example.ui_doan3tuan.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.ui_doan3tuan.databinding.ActivityLoginBinding
import com.example.ui_doan3tuan.model.LoginFormState
import com.example.ui_doan3tuan.model.LoginState
import com.example.ui_doan3tuan.viewmodel.LoginViewModel
import com.example.ui_doan3tuan.viewmodel.TokenManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TokenManager.init(applicationContext)
        if (TokenManager.isLoggedIn()) {
            navigateToHome()
            return
        }
        setupViews()
        setupObservers()
    }
    private fun setupViews() {
        setupEmailInput()
        setupPasswordInput()
        setupLoginButton()
        setupForgotPassword()
        setupSignUp()
    }
    private fun setupEmailInput() {
        binding.etEmail.doAfterTextChanged { text ->
            val email = text?.toString() ?: ""
            viewModel.onUsernameChange(email)
        }
    }
    private fun setupPasswordInput() {
        binding.etPassword.doAfterTextChanged { text ->
            val password = text?.toString() ?: ""
            viewModel.onPasswordChange(password)
        }
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            viewModel.login()
        }
    }

    private fun setupForgotPassword() {
        binding.tvForgotPassword.setOnClickListener {
            navigateToForgotPassword()
        }
    }

    private fun setupSignUp() {
        binding.tvSignUp.setOnClickListener {
            navigateToSignUp()
        }
    }
    private fun setupObservers() {
        observeFormState()
        observeLoginState()
    }

    private fun observeFormState() {
        lifecycleScope.launch {
            viewModel.formState.collect { formState ->
                updateFormUI(formState)
            }
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                handleLoginState(state)
            }
        }
    }



    private fun updateFormUI(formState: LoginFormState) {
        showEmailError(formState.usernameError)
        showPasswordError(formState.passwordError)
        binding.btnLogin.isEnabled = true
    }

    private fun showEmailError(errorMessage: String?) {
        binding.tilEmail.error = errorMessage
        binding.tilEmail.isErrorEnabled = errorMessage != null
    }

    private fun showPasswordError(errorMessage: String?) {
        binding.tilPassword.error = errorMessage
        binding.tilPassword.isErrorEnabled = errorMessage != null
    }

    private fun handleLoginState(state: LoginState) {
        when (state) {
            // 1. Trạng thái chờ
            is LoginState.Idle -> {

            }

            // 2. Đang xử lý
            is LoginState.Loading -> {
                showLoading(true)
            }

            // 3. Thành công
            is LoginState.Success -> {
                showLoading(false)
                showSuccessMessage(state.authResponse.message)
                navigateToHome()
            }

            // 4. Lỗi
            is LoginState.Error -> {
                showLoading(false)
                showErrorMessage(state.message, state.code)
                viewModel.resetState()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Hiển thị trạng thái loading
            binding.btnLogin.text = "ĐANG XỬ LÝ..."
            binding.btnLogin.isEnabled = false
        } else {
            // Ẩn trạng thái loading
            binding.btnLogin.text = "ĐĂNG NHẬP"
            binding.btnLogin.isEnabled = true
        }
    }

    private fun showSuccessMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.GREEN)
            .show()
    }

    private fun showErrorMessage(message: String, errorCode: Int?) {
        val userFriendlyMessage = getErrorMessage(message, errorCode)

        Snackbar.make(binding.root, userFriendlyMessage, Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.RED)
            .setAction("THỬ LẠI") {
                viewModel.login()
            }
            .show()
    }

    private fun getErrorMessage(message: String, errorCode: Int?): String {
        return when (errorCode) {
            400 -> "Vui lòng nhập đầy đủ thông tin"
            401 -> "Email/SĐT hoặc mật khẩu không đúng"
            500 -> "Lỗi server, vui lòng thử lại sau"
            else -> message
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)

    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)

    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { focusedView ->
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

}