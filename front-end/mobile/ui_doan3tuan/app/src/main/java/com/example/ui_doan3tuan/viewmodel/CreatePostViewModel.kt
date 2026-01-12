package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostPostModel
import com.example.ui_doan3tuan.model.PostResponseModel
import com.example.ui_doan3tuan.model.ResponsePostPostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class CreatePostViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }

    // Trạng thái chung cho cả quá trình đăng bài
    private val _postResult = MutableLiveData<Boolean>()
    val postResult: LiveData<Boolean> get() = _postResult

    // Hàm tổng hợp để xử lý toàn bộ quy trình
    fun publishFullPost(token: String, userId: Int, content: String, privacy: Int, files: List<File>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Bước 1: Upload ảnh (nếu có) và lấy danh sách URL
                val uploadedImageUrls = if (files.isNotEmpty()) {
                    uploadImagesSuspend(token,files) // Hàm chờ upload xong mới chạy tiếp
                } else {
                    emptyList()
                }

                // Bước 2: Tạo bài viết và lấy Post ID
                val newPostId = createPostSuspend(token, userId, content, privacy)
                Log.d("Test", "Vô 1")
                if (newPostId != null) {
                    // Bước 3: Gắn từng ảnh vào bài viết
                    Log.d("Test", "Vô 2")
                    uploadedImageUrls.forEach { imageUrl ->
                        Log.d("Test", "Vô 3")
                        createPostMediaSuspend(token, newPostId, imageUrl, 1)
                    }
                    _postResult.postValue(true)
                } else {
                    Log.d("Test", "Vô 4")
                    _postResult.postValue(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _postResult.postValue(false)
            }
        }
    }

    // --- Các hàm con dạng Suspend (Chạy ngầm và trả về kết quả) ---

    private fun uploadImagesSuspend(token: String, files: List<File>): List<String> {
        val urls = mutableListOf<String>()
        try {
            val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (file in files) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                multipartBuilder.addFormDataPart("images", file.name, requestFile)
            }
            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/images/upload")
                .addHeader("Authorization", "Bearer $token")
                .post(multipartBuilder.build())
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val json = JSONObject(bodyString ?: "")
                    if(json.has("files")) {
                        val filesArray = json.getJSONArray("files")
                        for (i in 0 until filesArray.length()) {
                            urls.add(filesArray.getString(i))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return urls
    }

    private fun createPostSuspend(token: String, userId: Int, content: String, privacy: Int): Int? {
        // 1. Log ngay đầu hàm để chứng minh code đã chạy vào đây
        Log.d("Test", "--- BẮT ĐẦU createPostSuspend ---")

        try {
            val reportBody = JSONObject()
                .put("user_id", userId)
                .put("content", content)
                .put("privacy", privacy)
                .put("location_id", 1)
                .put("status", 1)
                .toString()

            val mediaType = "application/json;charset=utf-8".toMediaType()
            val requestBody = reportBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/posts")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            Log.d("Test", "Đang gửi request tạo bài viết...")

            client.newCall(request).execute().use { resp ->
                Log.d("Test", "Response Code: ${resp.code}") // Log mã lỗi (200, 401, 500...)

                if (resp.isSuccessful) {
                    val bodyString = resp.body?.string().orEmpty()
                    Log.d("Test", "Response Body: $bodyString") // Log nội dung server trả về

                    // Coi chừng lỗi Parse JSON ở đây nếu PostPostModel không khớp
                    val data = json.decodeFromString<ResponsePostPostModel>(bodyString)
                    Log.d("Test", "ID bài viết mới: ${data.data.id}")
                    return data.data.id
                } else {
                    Log.d("Test", "Request thất bại: ${resp.message}")
                }
            }
        } catch (e: Exception) {
            // Log lỗi Exception chi tiết
            Log.e("Test", "LỖI CRASH trong createPostSuspend: ${e.message}")
            e.printStackTrace()
        }
        return null
    }

    private fun createPostMediaSuspend(token: String, postId: Int, imageUrl: String, mediaType: Int) {
        try {
            val reportBody = JSONObject().apply {
                put("post_id", postId)
                put("media_url", imageUrl)
                put("media_type", mediaType)
                put("thumbnail_url", "")
            }.toString()

            val reqType = "application/json;charset=utf-8".toMediaType()
            val requestBody = reportBody.toRequestBody(reqType)
            Log.d("Test", "Vô 5")
            val request = Request.Builder()
                .url("http://10.0.2.2:8989/api/post-medias")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()
            client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}