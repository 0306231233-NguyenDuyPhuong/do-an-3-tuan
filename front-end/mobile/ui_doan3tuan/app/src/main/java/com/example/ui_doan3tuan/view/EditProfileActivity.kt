package com.example.ui_doan3tuan.view

import android.content.Context
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
import com.example.ui_doan3tuan.viewmodel.CreatePostViewModel
import com.example.ui_doan3tuan.viewmodel.EditProfileViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.getValue
class EditProfileActivity : AppCompatActivity() {

    private var selectedImageFile: File? = null
    private lateinit var imgAvatar: ImageView
    private lateinit var txtUserName: EditText

    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            imgAvatar.setImageURI(uri)
            selectedImageFile = uriToFile(this, uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)


        imgAvatar = findViewById(R.id.imgAnhDaiDien)
        txtUserName = findViewById(R.id.edtTenCSHS)

        setupObservers()
        setupListeners()
        if (token.isNotEmpty()) {
            userProfileViewModel.getPostID(token, userId)
        }
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
        findViewById<TextView>(R.id.txtChinhSuaImage).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Nút Lưu
        findViewById<TextView>(R.id.txtLuu).setOnClickListener {
            val fullName = txtUserName.text.toString().trim()

            if (fullName.isEmpty()) {
                Snackbar.make(it, "Tên không được để trống", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Snackbar.make(it, "Đang lưu...", Snackbar.LENGTH_SHORT).show()
            editProfileViewModel.updateUserFull(token, fullName, selectedImageFile)
        }

        findViewById<TextView>(R.id.txtHuy).setOnClickListener {
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