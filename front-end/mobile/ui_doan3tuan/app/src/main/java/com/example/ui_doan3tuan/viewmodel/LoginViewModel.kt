
package com.example.ui_doan3tuan.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginViewModel : ViewModel() {

    // States
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    // Repository
    private val authRepository = AuthRepository()

    // Form handling
    fun onUsernameChange(username: String) {
        _formState.value = _formState.value.copy(username = username)
        validateForm()
    }

    fun onPasswordChange(password: String) {
        _formState.value = _formState.value.copy(password = password)
        validateForm()
    }

    private fun validateForm() {
        val currentState = _formState.value
        val username = currentState.username.trim()

        val isUsernameValid = when {
            username.isEmpty() -> false
            username.contains("@") -> android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()
            username.matches(Regex("\\d+")) -> username.length in 10..11
            else -> false
        }

        val isPasswordValid = currentState.password.length >= 8

        _formState.value = currentState.copy(
            isFormValid = true ,
            usernameError = when {
                username.isEmpty() -> "Email/SĐT là bắt buộc"
                !isUsernameValid && username.contains("@") -> "Email không hợp lệ"
                !isUsernameValid && username.matches(Regex("\\d+")) -> "SĐT phải có 10-11 số"
                !isUsernameValid -> "Email/SĐT không hợp lệ"
                else -> null
            },
            passwordError = when {
                currentState.password.isEmpty() -> "Mật khẩu là bắt buộc"
                currentState.password.length < 8 -> "Mật khẩu ít nhất 8 ký tự"

                else -> null
            }
        )
    }

    // Login action
    fun login() {
        val currentForm = _formState.value
        if (!currentForm.isFormValid) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = authRepository.login(
                username = currentForm.username.trim(),
                password = currentForm.password
            )

            _loginState.value = result.fold(
                onSuccess = { authResponse ->
                    // Save tokens and user
                    TokenManager.saveTokens(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    TokenManager.saveUser(authResponse.user)
                    LoginState.Success(authResponse)
                },
                onFailure = { error ->
                    when (error) {
                        is ApiException -> LoginState.Error(error.message, error.code)
                        else -> LoginState.Error(error.message ?: "Đăng nhập thất bại")
                    }
                }
            )
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

// API EXCEPTION
class ApiException(
    override val message: String,
    val code: Int
) : Exception(message)

// AUTH REPOSITORY
class AuthRepository {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Tạo request body
                val jsonBody = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }

                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                // 2. Tạo request
                val request = Request.Builder()
                    .url("http://10.0.2.2:8989/api/auth/login")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()

                // 3. Thực hiện request
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""

                    when (response.code) {
                        200 -> {
                            val json = JSONObject(responseBody)
                            val userJson = json.getJSONObject("user")

                            val authResponse = AuthResponse(
                                message = json.getString("message"),
                                accessToken = json.getString("accessToken"),
                                refreshToken = json.getString("refreshToken"),
                                user = User(
                                    id = userJson.getInt("id"),
                                    email = userJson.optString("email").takeIf { it != "null" && it.isNotEmpty() },
                                    phone = userJson.optString("phone").takeIf { it != "null" && it.isNotEmpty() },
                                    fullName = userJson.getString("full_name"),
                                    role = userJson.getString("role")
                                )
                            )
                            Result.success(authResponse)
                        }

                        400 -> Result.failure(
                            ApiException("Username and password are required", 400)
                        )

                        401 -> Result.failure(
                            ApiException("Invalid credentials", 401)
                        )

                        500 -> Result.failure(
                            ApiException("Internal server error", 500)
                        )

                        else -> Result.failure(
                            ApiException("Login failed: ${response.code}", response.code)
                        )
                    }
                }
            } catch (e: IOException) {
                Result.failure(ApiException("Network error: ${e.message}", 0))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

// TOKEN MANAGER
object TokenManager {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences("app_auth", Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        preferences.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            // Access token: 10 phút
            putLong("access_token_expiry", System.currentTimeMillis() + 10 * 60 * 1000)
            // Refresh token: 7 ngày
            putLong("refresh_token_expiry", System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
        }
    }

    fun saveUser(user: User) {
        preferences.edit {
            putInt("user_id", user.id)
            putString("user_email", user.email)
            putString("user_phone", user.phone)
            putString("user_full_name", user.fullName)
            putString("user_role", user.role)
        }
    }

    fun getAccessToken(): String? {
        val token = preferences.getString("access_token", null)
        val expiry = preferences.getLong("access_token_expiry", 0)
        return if (token != null && expiry > System.currentTimeMillis()) token else null
    }

    fun getRefreshToken(): String? {
        val token = preferences.getString("refresh_token", null)
        val expiry = preferences.getLong("refresh_token_expiry", 0)
        return if (token != null && expiry > System.currentTimeMillis()) token else null
    }

    fun getUser(): User? {
        val id = preferences.getInt("user_id", -1)
        if (id == -1) return null

        return User(
            id = id,
            email = preferences.getString("user_email", null),
            phone = preferences.getString("user_phone", null),
            fullName = preferences.getString("user_full_name", "")!!,
            role = preferences.getString("user_role", "user")!!
        )
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    fun logout() {
        preferences.edit {
            clear()
        }
    }
}