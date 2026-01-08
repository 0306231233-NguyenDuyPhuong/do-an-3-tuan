package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.PostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

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
        "like_count": 0,
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
                "media_url": "1767414890714-dalac.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:33.000Z",
                "updated_at": "2026-01-05T14:29:33.000Z",
                "post_id": 1
            },
            {
                "id": 2,
                "media_url": "1767414890714-dalac.png",
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
            {
                "id": 1,
                "media_url": "1767414890714-dalac.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:33.000Z",
                "updated_at": "2026-01-05T14:29:33.000Z",
                "post_id": 1
            },
            {
                "id": 2,
                "media_url": "1767414890714-dalac.png",
                "media_type": 0,
                "thumbnail_url": "",
                "deleted_at": null,
                "created_at": "2026-01-05T14:29:41.000Z",
                "updated_at": "2026-01-05T14:29:41.000Z",
                "post_id": 1
            }
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
}