package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterSelectImageAndVideo
import com.example.ui_doan3tuan.model.UserModel
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.CreatePostViewModel
import com.example.ui_doan3tuan.viewmodel.EditProfileViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.getValue
var avatarUrl2: String? = null
class EditProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var accessToken: String
    private var userId: Int = -1
    private var selectedImageFile: File? = null
    private lateinit var imgAvatar: ImageView
    private lateinit var txtUserName: EditText
    private lateinit var btnLuu: TextView
    private lateinit var btnHuy: TextView
    private lateinit var btnEditImage: TextView

    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            imgAvatar.setImageURI(uri)
            selectedImageFile = uriToFile(this, uri)
        } else {
            Log.d("Test", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        sessionManager = SessionManager(applicationContext)

        val tokenFromSession = sessionManager.getAccessToken()
        val user = sessionManager.getUser()

        if (tokenFromSession == null || user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        accessToken = tokenFromSession
        userId = user.id

        initViews()
        setupObservers()
        setupListeners()
        if (accessToken.isNotEmpty()) {
            userProfileViewModel.getPostID(accessToken, userId)
        }
    }
    private fun initViews(){
        imgAvatar = findViewById(R.id.imgAnhDaiDien)
        txtUserName = findViewById(R.id.edtTenCSHS)
        btnHuy = findViewById(R.id.txtHuy)
        btnLuu = findViewById(R.id.txtLuu)
        btnEditImage = findViewById(R.id.txtChinhSuaImage)
    }

    private fun setupObservers() {
        userProfileViewModel.postsId.observe(this) { listPosts ->
            if (!listPosts.isNullOrEmpty()) {
                val user = listPosts[0].User
                txtUserName.setText(user.full_name ?: "")

                // Load ảnh từ server (nếu chưa chọn ảnh mới)
                if (selectedImageFile == null) {
                    val avatarUrl = if (user.avatar.isNullOrEmpty()) {
                        R.drawable.profile // Ảnh mặc định trong drawable
                    } else {
                        "http://10.0.2.2:8989/api/images/${user.avatar}"
                    }
                    imgAvatar.load(avatarUrl) {
                        placeholder(R.drawable.profile)
                        error(R.drawable.profile)
                    }
                }
            }
        }
        editProfileViewModel.updateUser.observe(this) { isSuccess ->
            if (isSuccess) {
                var currentUser = sessionManager.getUser()
                var newName = txtUserName.text.toString().trim()
                currentUser.full_name = newName
                if (avatarUrl2 != null){
                    currentUser.avatar = avatarUrl2
                    sessionManager.updateAvatar(avatarUrl2 ?: "")
                }
                sessionManager.updateUserName(newName)
                Snackbar.make(txtUserName, "Cập nhật thành công!", Snackbar.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(txtUserName, "Cập nhật thất bại, vui lòng thử lại.", Snackbar.LENGTH_SHORT).show()
            }
        }
        editProfileViewModel.error.observe(this) { errorMsg ->
            Snackbar.make(txtUserName, "Lỗi: $errorMsg", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        btnEditImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        // Nút Lưu
        btnLuu.setOnClickListener {
            val fullName = txtUserName.text.toString().trim()
            if (fullName.isEmpty()) {
                Snackbar.make(it, "Tên không được để trống", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Snackbar.make(it, "Đang lưu...", Snackbar.LENGTH_SHORT).show()
            editProfileViewModel.updateUserFull(accessToken, fullName, selectedImageFile)
        }
        btnHuy.setOnClickListener {
            finish()
        }
    }
    private fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}