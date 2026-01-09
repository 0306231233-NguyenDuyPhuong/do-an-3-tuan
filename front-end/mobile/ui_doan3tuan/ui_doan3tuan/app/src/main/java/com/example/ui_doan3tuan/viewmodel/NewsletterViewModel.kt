package com.example.ui_doan3tuan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.ListCommentModel
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.PostResponse
import com.example.ui_doan3tuan.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NewsletterViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val json: Json = Json { ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true }
    val jsonBody = """{
    "message": "Get post success",
    "data": [
        {
        "id": 1,
        "content": "Thành phố Hồ Chí Minh thật đẹp",
        "privacy": 0,
        "status": 0,
        "like_count": 10,
        "comment_count": 0,
        "share_count": 0,
        "created_at": "2026-01-05T14:16:18.000Z",
        "updated_at": "2026-01-05T14:16:18.000Z",
        "location_id": 1,
        "user_id": 1,
        "User": {
            "id": 1,
            "full_name": null,
            "avatar": null,
            "status": 0
        },
        "Location": {
            "id": 1,
            "name": "An Giang",
            "address": "Châu Đốc",
            "deleted_at": null,
            "created_at": "2026-01-05T14:16:03.000Z",
            "updated_at": "2026-01-05T14:25:58.000Z"
        },
        "PostMedia": [
            {
                "id": 1,
                "media_url": "Screenshot 2026-01-03 143630.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:33.000Z",
                "updated_at": "2026-01-05T14:29:33.000Z",
                "post_id": 1
            },
            {
                "id": 2,
                "media_url": "Screenshot 2026-01-03 143630.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:41.000Z",
                "updated_at": "2026-01-05T14:29:41.000Z",
                "post_id": 1
            },{
                "id": 3,
                "media_url": "Screenshot 2026-01-03 143630.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:41.000Z",
                "updated_at": "2026-01-05T14:29:41.000Z",
                "post_id": 1
            }
        ]
    },
    {
        "id": 2,
        "content": "Thành phố Hà Nội thật đẹp",
        "privacy": 0,
        "status": 0,
        "like_count": 0,
        "comment_count": 0,
        "share_count": 0,
        "created_at": "2026-01-05T14:16:18.000Z",
        "updated_at": "2026-01-05T14:16:18.000Z",
        "location_id": 2,
        "user_id": 2,
        "User": {
            "id": 1,
            "full_name":"Nguyễn Tấn Toàn",
            "avatar": null,
            "status": 0
        },
        "Location": {
            "id": 1,
            "name": "An Giang",
            "address": "Châu Đốc",
            "deleted_at": null,
            "created_at": "2026-01-05T14:16:03.000Z",
            "updated_at": "2026-01-05T14:25:58.000Z"
        },
        "PostMedia": [
            
        ]
    }
]
}
""";

    private val _posts = MutableLiveData<List<PostModel>>()
    val posts: LiveData<List<PostModel>> get() = _posts
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = json.decodeFromString<PostResponse>(jsonBody)
                val list = response.data as List<PostModel>
                // Cập nhật vào LiveData (Dùng postValue vì đang ở Dispatchers.IO)
                _posts.postValue(list)

            } catch (e: Exception) {
                _error.postValue("Kết nối mạng không ổn định, vui lòng thử lại sau!")
            }
        }
    }
//    // Trong NewsletterViewModel
//    fun sendComment(postId: Int, content: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val commentBody = """
//                {
//                    "post_id": $postId,
//                    "content": "$content"
//                }
//            """.trimIndent()
//                val JSON = "application/json;charset=utf-8".toMediaType();
//                val requestBody = commentBody.toRequestBody(JSON);
//                val request = Request.Builder()
//                    .url("http://10.0.2.2:8989/api/comments")
//                    .post(requestBody)
//                    .build()
//                val response = client.newCall(request).execute()
//                if (response.isSuccessful) {
//
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
fun sendComment(postId: Int, content: String) {
    // Tạo một comment giả (Fake)
    // Vì chưa có API thật trả về ID và ngày giờ, ta tự bịa ra
    val newComment = CommentModel(
        id = (System.currentTimeMillis() / 1000).toInt(), // ID ngẫu nhiên
        post_id = postId,
        user_id = 999, // ID của chính mình
        content = content,
        status = 1,
        created_at = "Vừa xong", // Hoặc lấy ngày giờ hiện tại
        User = UserModel(id = 999,
            full_name = "Tôi (Current User)", // Tên của người đang dùng app
            avatar = null,
            status = 1)
    )


    // 2. Thêm vào danh sách tạm trong bộ nhớ
    currentCommentList.add(newComment)

    // 3. Cập nhật LiveData -> UI sẽ tự động hiện comment mới
    _comments.postValue(currentCommentList)

}
    private val _comments = MutableLiveData<List<CommentModel>>()
    val comments: LiveData<List<CommentModel>> get() = _comments
    private var currentCommentList = mutableListOf<CommentModel>()
    val listComment = """{
  "data": [
    {
      "id": 1,
      "post_id": 1,
      "user_id": 1,
      "content": "Bài viết hay quá",
      "status": 1,
      "created_at": "2026-01-04T10:00:00Z",
      "User": {
        "id": 3,
        "full_name": "Nguyen Van A",
        "avatar": "avatar.png"
        
      }
    },
    {
      "id": 2,
      "post_id": 1,
      "user_id": 1,
      "content": "Bài viết hay quá",
      "status": 1,
      "created_at": "2026-01-04T10:00:00Z",
      "User": {
        "id": 3,
        "full_name": "Nguyen Van B",
        "avatar": "avatar.png"
      }
    }
  ]
}
""";
    // Trong NewsletterViewModel.kt

    fun getCommentsByPostId(postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = json.decodeFromString<ListCommentModel>(listComment)
                currentCommentList = list.data.toMutableList()
                _comments.postValue(list.data)

            } catch (e: Exception) {
                Log.e("DEBUG_APP", "LỖI PARSE JSON: ${e.message}")
                e.printStackTrace()
                _comments.postValue(emptyList())
            }
        }
    }

//    fun getCommentsByPostId(postId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
////                val request = Request.Builder()
////                    .url("http://10.0.2.2:8989/api/comments/$postId")
////                    .get()
////                    .build()
////
////                val response = client.newCall(request).execute()
////                if (response.isSuccessful) {
////                    val jsonString = response.body?.string()
////
////                    val list = json.decodeFromString<ListCommentModel>(listComment ?: "[]")
////                    _comments.postValue(list.data)
////                }
//                val list = json.decodeFromString<ListCommentModel>(listComment ?: "[]")
//                Log.d("test", "${list.data}")
//                    _comments.postValue(list.data)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

}