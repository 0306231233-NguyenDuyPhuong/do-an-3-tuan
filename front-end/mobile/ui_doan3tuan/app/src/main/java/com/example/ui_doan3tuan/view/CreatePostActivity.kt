package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterSelectImageAndVideo
import com.example.ui_doan3tuan.viewmodel.CreatePostViewModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.getValue
class CreatePostActivity : AppCompatActivity() {
    private lateinit var adapterChonAnhVideo: AdapterSelectImageAndVideo
    private val viewModel: CreatePostViewModel by viewModels()
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                val currentList = adapterChonAnhVideo.currentList
                val updatedList = (currentList + uris).distinct()
                adapterChonAnhVideo.submitList(updatedList)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)
        adapterChonAnhVideo = AdapterSelectImageAndVideo { uriToDelete ->
            val newList = adapterChonAnhVideo.currentList.toMutableList()
            newList.remove(uriToDelete)
            adapterChonAnhVideo.submitList(newList)
        }

        setupDropdownStatus()

        val revHienThiAnhView = findViewById<RecyclerView>(R.id.revHienAnhVideo)
        revHienThiAnhView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        revHienThiAnhView.adapter = adapterChonAnhVideo


        findViewById<Button>(R.id.btnChonAnhVideo).setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }
        Log.d("token", "$token")
        viewModel.postResult.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NewsletterActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Đăng bài thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.error.observe(this) { error ->
            if (error == "TOKEN_EXPIRED") {
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }


        val content = findViewById<EditText>(R.id.edtContent)
        findViewById<Button>(R.id.btnDangBai).setOnClickListener {
            val finalImagesUri = adapterChonAnhVideo.currentList
            val dropdownStatus = findViewById<AutoCompleteTextView>(R.id.dropdownStatus)
            Log.d("dropdownStatus", "${dropdownStatus.text}")
            Log.d("dropdownStatus", "${finalImagesUri}")
            val privacy = when (dropdownStatus.text.toString()) {
                "Công khai" -> 0
                "Bạn bè" -> 1
                "Chỉ mình tôi" -> 2
                else -> 0
            }
            val listFiles = ArrayList<File>()
            if (finalImagesUri.isNotEmpty()) {
                for (uriString in finalImagesUri) {
                    val uri = Uri.parse(uriString.toString())
                    val file = uriToFile(this, uri)
                    if (file != null) {
                        listFiles.add(file)
                    }
                }
            }

            viewModel.publishFullPost(token, userId, content.text.toString(), privacy, listFiles)
            Log.d("token", "test $token")
        }


        findViewById<ImageView>(R.id.imgThoatTaoBaiDang).setOnClickListener {
            finish()
        }
    }

    private fun setupDropdownStatus() {
        val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdownStatus)
        val list = listOf("Công khai", "Bạn bè", "Chỉ mình tôi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        dropdown.setAdapter(adapter)
        dropdown.setOnItemClickListener { _, _, position, _ ->
            val iconRes = when (position) {
                0 -> R.drawable.baseline_public_24
                1 -> R.drawable.outline_group_24
                2 -> R.drawable.outline_lock_24
                else -> R.drawable.baseline_public_24
            }
            dropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0)
        }

    }
    fun uriToFile(context: Context, uri: Uri): File? {
        try {
            // 1. Mở luồng đọc dữ liệu từ URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            // 2. Tạo file tạm trong bộ nhớ cache của app (để không cần xin quyền write storage)
            // Đặt tên file ngẫu nhiên hoặc theo timestamp để tránh trùng
            val tempFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")

            // 3. Copy dữ liệu từ luồng đọc sang file tạm
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