package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.PostAdapter
import com.example.ui_doan3tuan.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsProfileActivity : AppCompatActivity() {

    private lateinit var btnKetBan: Button
    private lateinit var btnNhanTin: Button
    private lateinit var postAdapter: PostAdapter

    private var friendId: Int = -1
    private var friendName: String = ""
    private var isFriend: Boolean = false
    private var hasSentRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        getDataFromIntent()
        val recyclerView = findViewById<RecyclerView>(R.id.rev_baidang)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(mutableListOf())
        recyclerView.adapter = postAdapter
        findViews()
        showInfo()
        setupButtons()


        checkFriendStatusFromAPI()
    }

    private fun getDataFromIntent() {
        friendId = intent.getIntExtra("friend_id", -1)
        friendName = intent.getStringExtra("friend_name") ?: "Chưa có tên"
        isFriend = intent.getBooleanExtra("from_friends_list", false)
    }

    private fun findViews() {
        btnKetBan = findViewById(R.id.btnKetBan)
        btnNhanTin = findViewById(R.id.btnNhanTin)
    }

    private fun showInfo() {
        findViewById<TextView>(R.id.textView9).text = friendName

        val avatar = intent.getStringExtra("friend_avatar")
        val imgAvatar = findViewById<ImageView>(R.id.imageView9)

        Glide.with(this)
            .load(avatar)
            .placeholder(R.drawable.profile)
            .into(imgAvatar)
    }

    // ================= FRIEND STATUS =================

    private fun checkFriendStatusFromAPI() {
        val token = getToken()
        if (token.isEmpty()) return

        ApiClient.apiService.getFriendList("Bearer $token")
            .enqueue(object : Callback<FriendListResponse> {
                override fun onResponse(
                    call: Call<FriendListResponse>,
                    response: Response<FriendListResponse>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()?.data ?: emptyList()
                        isFriend = list.any { it.id == friendId }

                        updateButtonUI()

                        if (isFriend) {
                            loadFriendPosts()
                        }
                    }
                }

                override fun onFailure(call: Call<FriendListResponse>, t: Throwable) {
                    Log.e("FriendsProfile", "Check friend error", t)
                }
            })
    }

private fun loadFriendPosts() {
    val token = getToken()
    if (token.isEmpty()) return

    ApiClient.apiService
        .getPostsByUser("Bearer $token", friendId)
        .enqueue(object : Callback<UserPostResponse> {

            override fun onResponse(
                call: Call<UserPostResponse>,
                response: Response<UserPostResponse>
            ) {
                // 🔥 LOG KIỂM TRA
                Log.d("API_CHECK", "code = ${response.code()}")
                Log.d("API_CHECK", "raw error = ${response.errorBody()?.string()}")
                Log.d("API_CHECK", "body = ${response.body()}")

                if (response.isSuccessful) {
                    val posts = response.body()?.posts ?: emptyList()
                    Log.d("API_CHECK", "posts size = ${posts.size}")

                    postAdapter.setData(posts)
                }
            }

            override fun onFailure(call: Call<UserPostResponse>, t: Throwable) {
                Log.e("API_CHECK", "API FAIL", t)
            }
        })
}

    private fun setupButtons() {
        updateButtonUI()

        btnNhanTin.setOnClickListener {
            if (!isFriend) {
                Toast.makeText(this, "Cần kết bạn trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("id", friendId)
                putExtra("full_name", friendName)
            })
        }

        btnKetBan.setOnClickListener {
            if (isFriend) showUnfriendDialog()
            else sendFriendRequest()
        }

        findViewById<ImageView>(R.id.imgThoatFP).setOnClickListener {
            finish()
        }
    }

    private fun updateButtonUI() {
        when {
            isFriend -> {
                btnKetBan.text = "Bạn bè"
                btnNhanTin.isEnabled = true
            }
            hasSentRequest -> {
                btnKetBan.text = "Đã gửi lời mời"
                btnKetBan.isEnabled = false
            }
            else -> {
                btnKetBan.text = "Kết bạn"
                btnKetBan.isEnabled = true
                btnNhanTin.isEnabled = false
            }
        }
    }


    private fun sendFriendRequest() {
        val token = getToken()
        if (token.isEmpty()) return

        ApiClient.apiService.sendFriendRequest(
            "Bearer $token",
            SendRequest(friendId)
        ).enqueue(object : Callback<ApiMessage> {
            override fun onResponse(call: Call<ApiMessage>, response: Response<ApiMessage>) {
                if (response.isSuccessful) {
                    hasSentRequest = true
                    updateButtonUI()
                }
            }

            override fun onFailure(call: Call<ApiMessage>, t: Throwable) {}
        })
    }

    private fun showUnfriendDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hủy kết bạn")
            .setMessage("Bạn có chắc muốn hủy kết bạn?")
            .setPositiveButton("Có") { _, _ -> unfriend() }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun unfriend() {
        val token = getToken()
        if (token.isEmpty()) return

        ApiClient.apiService.unfriend(
            "Bearer $token",
            UnfriendRequest(friendId)
        ).enqueue(object : Callback<ApiMessage> {
            override fun onResponse(call: Call<ApiMessage>, response: Response<ApiMessage>) {
                if (response.isSuccessful) {
                    isFriend = false
                    postAdapter.setData(emptyList())
                    updateButtonUI()
                }
            }

            override fun onFailure(call: Call<ApiMessage>, t: Throwable) {}
        })
    }

    private fun getToken(): String {
        return getSharedPreferences("user_data", MODE_PRIVATE)
            .getString("access_token", "") ?: ""
    }
}
