package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.FriendsAdapter
import com.example.ui_doan3tuan.model.Friend
import com.example.ui_doan3tuan.model.FriendListResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var adapter: FriendsAdapter
    private var friendsList = mutableListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        recyclerView = findViewById(R.id.rvFriends)
        editTextSearch = findViewById(R.id.etSearch)
        setupRecyclerView()

        loadFriends()
        findViewById<ImageView>(R.id.imgThoatLF).setOnClickListener {
            finish()
        }

        // Xử lý nút thêm bạn
        findViewById<ImageView>(R.id.imgAddFriend).setOnClickListener {
            val intent = Intent(this, FriendsAddListActivity::class.java)
            startActivity(intent)
        }

        setupBottomNav()
    }

    private fun setupRecyclerView() {
        adapter = FriendsAdapter(friendsList) { friend ->
            // Khi click vào một bạn bè
            val intent = Intent(this, FriendsProfileActivity::class.java)
            intent.putExtra("friend_id", friend.id)
            intent.putExtra("friend_name", friend.full_name)
            intent.putExtra("friend_avatar", friend.avatar)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadFriends() {
        // Lấy token từ SharedPreferences
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        val token = sharedPref.getString("access_token", "")


        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            return
        }

        val fullToken = "Bearer $token"
        Log.d("test"," load thành công")

        ApiClient.apiService.getFriendList(fullToken).enqueue(object : Callback<FriendListResponse> {
            override fun onResponse(
                call: Call<FriendListResponse>,
                response: Response<FriendListResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.data.isNotEmpty()) {
                        friendsList.clear()
                        friendsList.addAll(data.data)
                        adapter.notifyDataSetChanged()
                        Log.d("test"," load thành công")
                    } else {
                        Toast.makeText(this@FriendsListActivity, "Bạn chưa có bạn bè nào", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@FriendsListActivity, "Lỗi: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.d("test"," load lỗi")
                }
            }

            override fun onFailure(call: Call<FriendListResponse>, t: Throwable) {
                Toast.makeText(this@FriendsListActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_friend

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, NewsletterActivity::class.java))
                    true
                }
                R.id.nav_friend -> {
                    // Đã ở trang bạn bè
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, CreatePostActivity::class.java))
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}