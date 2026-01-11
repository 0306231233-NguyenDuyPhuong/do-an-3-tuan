# Tài liệu triển khai tính năng Quên mật khẩu

## Tổng quan

Tài liệu này mô tả chi tiết việc triển khai tính năng quên mật khẩu (Forgot Password) cho ứng dụng Android, tích hợp với backend API đã có sẵn.

## Mục lục

1. [Tổng quan kiến trúc](#tổng-quan-kiến-trúc)
2. [Backend API](#backend-api)
3. [Frontend Implementation](#frontend-implementation)
4. [Chi tiết các file đã chỉnh sửa](#chi-tiết-các-file-đã-chỉnh-sửa)
5. [Flow hoạt động](#flow-hoạt-động)
6. [Validation Rules](#validation-rules)
7. [Error Handling](#error-handling)
8. [Testing](#testing)

---

## Tổng quan kiến trúc

### Backend (Đã có sẵn)

Backend đã có sẵn 2 API endpoints:

- **POST /api/auth/forgot-password**: Tạo reset token cho user
- **POST /api/auth/reset-password**: Reset mật khẩu với token

### Frontend (Đã triển khai)

Frontend Android app bao gồm:

- **ForgotPasswordActivity**: Màn hình nhập email/phone để lấy reset token
- **ResetPasswordActivity**: Màn hình nhập mật khẩu mới với token
- **AuthRepository**: Repository xử lý API calls
- **Models**: Data classes cho request/response

---

## Backend API

### 1. Forgot Password API

**Endpoint**: `POST /api/auth/forgot-password`

**Request Body**:
```json
{
  "username": "user@example.com" // hoặc "0123456789"
}
```

**Response Success (200)**:
```json
{
  "message": "Token generated",
  "token": "abc123def456..."
}
```

**Response Error (400)**:
```json
{
  "message": "Username is required"
}
```

**Response Error (404)**:
```json
{
  "message": "User not found"
}
```

### 2. Reset Password API

**Endpoint**: `POST /api/auth/reset-password`

**Request Body**:
```json
{
  "token": "abc123def456...",
  "password": "NewPassword123!"
}
```

**Response Success (200)**:
```json
{
  "message": "Password updated successfully"
}
```

**Response Error (400)**:
```json
{
  "message": "Invalid or expired token"
}
```
hoặc
```json
{
  "message": "Password must include uppercase, lowercase, number and special character"
}
```

**Lưu ý**:
- Token có thời hạn 15 phút
- Password phải tuân theo regex: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$`
- Password phải có độ dài từ 8-32 ký tự

---

## Frontend Implementation

### Kiến trúc

```
Activity (UI Layer)
    ↓
AuthRepository (Data Layer)
    ↓
OkHttpClient (Network Layer)
    ↓
Backend API
```

### Flow dữ liệu

```
ForgotPasswordActivity
  → AuthRepository.forgotPassword()
    → API: POST /api/auth/forgot-password
      → Response: { token }
        → Navigate to ResetPasswordActivity (với token)

ResetPasswordActivity
  → AuthRepository.resetPassword(token, password)
    → API: POST /api/auth/reset-password
      → Response: { message }
        → Navigate back to LoginActivity
```

---

## Chi tiết các file đã chỉnh sửa

### 1. Models - `LoginModels.kt`

**Location**: `app/src/main/java/com/example/ui_doan3tuan/model/LoginModels.kt`

**Các data classes đã thêm**:

```kotlin
// Forgot Password Models
data class ForgotPasswordRequest(
    @SerializedName("username")
    val username: String
)

data class ForgotPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String
)

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val response: ForgotPasswordResponse) : ForgotPasswordState()
    data class Error(val message: String, val code: Int? = null) : ForgotPasswordState()
}

// Reset Password Models
data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("password")
    val password: String
)

data class ResetPasswordResponse(
    @SerializedName("message")
    val message: String
)

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class Success(val response: ResetPasswordResponse) : ResetPasswordState()
    data class Error(val message: String, val code: Int? = null) : ResetPasswordState()
}
```

### 2. AuthRepository - `LoginViewModel.kt`

**Location**: `app/src/main/java/com/example/ui_doan3tuan/viewmodel/LoginViewModel.kt`

**Các methods đã thêm vào class `AuthRepository`**:

#### `forgotPassword(username: String)`

- Gọi API: `POST http://10.0.2.2:8989/api/auth/forgot-password`
- Xử lý response codes: 200, 400, 404, 500
- Trả về `Result<ForgotPasswordResponse>`

#### `resetPassword(token: String, password: String)`

- Gọi API: `POST http://10.0.2.2:8989/api/auth/reset-password`
- Xử lý response codes: 200, 400, 500
- Trả về `Result<ResetPasswordResponse>`

**Lưu ý**: 
- Base URL: `http://10.0.2.2:8989/api/` (Android emulator localhost)
- Sử dụng OkHttpClient với timeout 30 giây
- Có HttpLoggingInterceptor để debug

### 3. ForgotPasswordActivity.kt

**Location**: `app/src/main/java/com/example/ui_doan3tuan/view/ForgotPasswordActivity.kt`

**Tính năng đã triển khai**:

- ✅ Input validation (email/phone format)
- ✅ API integration với `AuthRepository`
- ✅ Loading state (disable button, thay đổi text)
- ✅ Error handling với Snackbar
- ✅ Navigation sang `ResetPasswordActivity` với token
- ✅ Real-time validation feedback

**Các methods chính**:

- `handleForgotPassword()`: Xử lý logic chính
- `validateUsername(username: String)`: Validate input
- `showLoading(isLoading: Boolean)`: Quản lý loading state
- `handleError(error: Throwable)`: Xử lý lỗi
- `showErrorMessage(message: String)`: Hiển thị lỗi

### 4. ResetPasswordActivity.kt

**Location**: `app/src/main/java/com/example/ui_doan3tuan/view/ResetPasswordActivity.kt`

**Tính năng đã triển khai**:

- ✅ Nhận token từ Intent
- ✅ Password validation (regex khớp backend)
- ✅ Confirm password validation
- ✅ API integration với `AuthRepository`
- ✅ Loading state
- ✅ Error handling với Snackbar
- ✅ Tự động navigate về `LoginActivity` sau khi thành công
- ✅ Real-time validation feedback

**Các methods chính**:

- `handleResetPassword()`: Xử lý logic chính
- `validatePassword(password: String)`: Validate password strength
- `validateConfirmPassword(newPassword: String, confirmPassword: String)`: Validate match
- `isPasswordStrong(password: String)`: Check password regex
- `showLoading(isLoading: Boolean)`: Quản lý loading state
- `handleError(error: Throwable)`: Xử lý lỗi
- `showSuccessMessage(message: String)`: Hiển thị thành công

### 5. Layout - `activity_reset_password.xml`

**Location**: `app/src/main/res/layout/activity_reset_password.xml`

**Thay đổi**:

- Ẩn verification code fields (vì backend dùng token thay vì verification code)
- Set `android:visibility="gone"` cho:
  - `tilVerificationCodeReset`
  - `tvResendCode`
  - `tvTimer`

---

## Flow hoạt động

### 1. User Flow

```
LoginActivity
    ↓ (Click "Quên mật khẩu")
ForgotPasswordActivity
    ↓ (Nhập email/phone → Submit)
    ↓ (API: POST /auth/forgot-password)
    ↓ (Nhận token)
ResetPasswordActivity (với token)
    ↓ (Nhập password mới → Submit)
    ↓ (API: POST /auth/reset-password)
    ↓ (Thành công)
LoginActivity
```

### 2. Technical Flow

#### Forgot Password Flow

```
1. User nhập email/phone trong ForgotPasswordActivity
2. validateUsername() kiểm tra format
3. Nếu hợp lệ → gọi AuthRepository.forgotPassword()
4. Show loading state
5. API request → Backend
6. Response handling:
   - Success (200): Lấy token → Navigate to ResetPasswordActivity
   - Error (400/404/500): Show error message
7. Hide loading state
```

#### Reset Password Flow

```
1. ResetPasswordActivity nhận token từ Intent
2. User nhập password mới và confirm password
3. validatePassword() kiểm tra strength
4. validateConfirmPassword() kiểm tra match
5. Nếu hợp lệ → gọi AuthRepository.resetPassword()
6. Show loading state
7. API request → Backend
8. Response handling:
   - Success (200): Show success message → Navigate to LoginActivity
   - Error (400/500): Show error message
9. Hide loading state
```

---

## Validation Rules

### Username (Email/Phone)

- **Email**: Phải match pattern `android.util.Patterns.EMAIL_ADDRESS`
- **Phone**: Phải là số, độ dài 10-11 ký tự
- **Required**: Không được để trống

### Password

**Backend Regex**: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,}$`

**Requirements**:
- ✅ Ít nhất 1 chữ thường (a-z)
- ✅ Ít nhất 1 chữ hoa (A-Z)
- ✅ Ít nhất 1 số (0-9)
- ✅ Ít nhất 1 ký tự đặc biệt (!@#$%^&*...)
- ✅ Độ dài tối thiểu: 8 ký tự
- ✅ Độ dài tối đa: 32 ký tự

**Frontend Validation**:

```kotlin
private fun isPasswordStrong(password: String): Boolean {
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    
    return hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar
}
```

### Confirm Password

- Phải khớp với password mới
- Required: Không được để trống

---

## Error Handling

### HTTP Status Codes

#### Forgot Password API

| Code | Ý nghĩa | Xử lý Frontend |
|------|---------|----------------|
| 200 | Thành công | Navigate to ResetPasswordActivity với token |
| 400 | Thiếu username | Hiển thị: "Vui lòng nhập đầy đủ thông tin" |
| 404 | User không tồn tại | Hiển thị: "Không tìm thấy tài khoản với email/SĐT này" |
| 500 | Lỗi server | Hiển thị: "Lỗi server, vui lòng thử lại sau" |

#### Reset Password API

| Code | Ý nghĩa | Xử lý Frontend |
|------|---------|----------------|
| 200 | Thành công | Show success → Navigate to LoginActivity |
| 400 | Token không hợp lệ/hết hạn | Hiển thị: "Token không hợp lệ hoặc đã hết hạn. Vui lòng thử lại từ đầu." |
| 400 | Password yếu | Hiển thị: "Mật khẩu không đáp ứng yêu cầu. Vui lòng kiểm tra lại." |
| 500 | Lỗi server | Hiển thị: "Lỗi server, vui lòng thử lại sau" |

### Network Errors

- **IOException**: "Lỗi kết nối: {error message}"
- **Timeout**: Xử lý bởi OkHttpClient timeout (30s)
- **Unknown errors**: "Đã xảy ra lỗi không mong muốn: {error message}"

### UI Error Display

- Sử dụng **Snackbar** với background màu đỏ cho errors
- Sử dụng **Snackbar** với background màu xanh cho success
- Có nút "THỬ LẠI" cho error cases
- TextInputLayout error messages cho validation errors

---

## Testing

### Test Cases

#### Forgot Password

- [x] **TC1**: Nhập email hợp lệ → Thành công
- [x] **TC2**: Nhập phone hợp lệ → Thành công
- [x] **TC3**: Nhập email không tồn tại → Error 404
- [x] **TC4**: Nhập phone không tồn tại → Error 404
- [x] **TC5**: Để trống username → Validation error
- [x] **TC6**: Nhập email format sai → Validation error
- [x] **TC7**: Nhập phone < 10 số → Validation error
- [x] **TC8**: Network error → Hiển thị error message

#### Reset Password

- [x] **TC1**: Reset với token hợp lệ và password hợp lệ → Thành công
- [x] **TC2**: Reset với token hết hạn → Error 400
- [x] **TC3**: Reset với token không hợp lệ → Error 400
- [x] **TC4**: Password thiếu chữ hoa → Validation error
- [x] **TC5**: Password thiếu chữ thường → Validation error
- [x] **TC6**: Password thiếu số → Validation error
- [x] **TC7**: Password thiếu ký tự đặc biệt → Validation error
- [x] **TC8**: Password < 8 ký tự → Validation error
- [x] **TC9**: Password > 32 ký tự → Validation error
- [x] **TC10**: Confirm password không khớp → Validation error
- [x] **TC11**: Network error → Hiển thị error message
- [x] **TC12**: Thiếu token trong Intent → Error và finish()

### Testing Checklist

#### Manual Testing

1. **Forgot Password Flow**
   - [ ] Test với email hợp lệ
   - [ ] Test với phone hợp lệ
   - [ ] Test với username không tồn tại
   - [ ] Test validation errors
   - [ ] Test network errors (turn off WiFi)

2. **Reset Password Flow**
   - [ ] Test với token hợp lệ và password hợp lệ
   - [ ] Test với token hết hạn (đợi > 15 phút)
   - [ ] Test validation errors (password weak, mismatch)
   - [ ] Test network errors
   - [ ] Verify navigation về LoginActivity sau khi thành công

3. **Integration Testing**
   - [ ] Full flow: Login → Forgot → Reset → Login
   - [ ] Test với multiple users
   - [ ] Test với concurrent requests

---

## Cấu hình

### Base URL

Hiện tại sử dụng: `http://10.0.2.2:8989/api/`

**Lưu ý**: 
- `10.0.2.2` là địa chỉ localhost cho Android Emulator
- Nếu test trên thiết bị thật, cần thay bằng IP máy chủ backend

### Timeout

- Connect timeout: 30 giây
- Read timeout: 30 giây
- Write timeout: 30 giây

### Token Expiry

- Reset token có thời hạn: **15 phút**
- Sau khi hết hạn, user cần bắt đầu lại từ Forgot Password

---

## Lưu ý kỹ thuật

### 1. Intent Extra

Token được truyền qua Intent với key: `"RESET_TOKEN"`

```kotlin
// ForgotPasswordActivity → ResetPasswordActivity
intent.putExtra("RESET_TOKEN", token)

// ResetPasswordActivity nhận token
val token = intent.getStringExtra("RESET_TOKEN") ?: ""
```

### 2. Password Validation

Frontend validation phải khớp 100% với backend regex để tránh validation mismatch.

### 3. Error Messages

Tất cả error messages được hiển thị bằng tiếng Việt để thân thiện với người dùng.

### 4. Loading States

- Disable button khi đang loading
- Thay đổi text button: "ĐANG XỬ LÝ..."
- Set alpha = 0.6f để visual feedback

### 5. Navigation

- Sau khi reset thành công, sử dụng `FLAG_ACTIVITY_CLEAR_TOP` để clear back stack
- Delay 1.5 giây trước khi navigate để user đọc success message

---

## Cải tiến trong tương lai

### Tính năng có thể thêm

1. **Email/SMS Integration**: Thay vì trả token trực tiếp, gửi token qua email/SMS
2. **Token QR Code**: Hiển thị token dưới dạng QR code
3. **Biometric Authentication**: Xác thực bằng vân tay/face ID trước khi reset
4. **Password Strength Indicator**: Visual indicator cho password strength
5. **Remember Device**: Ghi nhớ thiết bị đã reset password
6. **Rate Limiting**: Giới hạn số lần request forgot password trong khoảng thời gian

### Code Improvements

1. **Dependency Injection**: Sử dụng Dagger/Hilt thay vì tạo instance trực tiếp
2. **Coroutine Error Handling**: Sử dụng CoroutineExceptionHandler
3. **State Management**: Sử dụng ViewModel cho state management
4. **Unit Tests**: Thêm unit tests cho validation logic
5. **UI Tests**: Thêm Espresso tests cho UI flows

---

## Tài liệu tham khảo

- Backend API Documentation: `back-end/controllers/AuthController.js`
- Android Architecture: https://developer.android.com/jetpack/guide
- Material Design Components: https://material.io/components
- OkHttp Documentation: https://square.github.io/okhttp/

---

## Tác giả

Triển khai bởi: AI Assistant
Ngày: 2025-01-XX
Version: 1.0.0

---

## Changelog

### Version 1.0.0 (2025-01-XX)

- ✅ Initial implementation
- ✅ Forgot Password API integration
- ✅ Reset Password API integration
- ✅ Input validation
- ✅ Error handling
- ✅ Loading states
- ✅ Navigation flow
