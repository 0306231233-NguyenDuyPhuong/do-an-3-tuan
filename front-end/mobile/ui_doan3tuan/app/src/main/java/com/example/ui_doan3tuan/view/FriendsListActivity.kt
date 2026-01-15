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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class FriendsListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var adapter: FriendsAdapter
    private val friendsList = mutableListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        recyclerView = findViewById(R.id.rvFriends)
        editTextSearch = findViewById(R.id.etSearch)

        setupRecyclerView()

        // Load danh sách bạn bè
        loadFriends()

        // Xử lý nút thoát
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
    override fun onResume() {
        super.onResume()
        loadFriends()
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
            // Có thể chuyển về màn hình login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val fullToken = "Bearer $token"
        Log.d("API_DEBUG", "Token: $token")
        Log.d("API_DEBUG", "Full token: $fullToken")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("API_DEBUG", "Đang gọi API...")
                val response: Response<FriendListResponse> =
                    ApiClient.apiService.getFriendList(fullToken).execute()

                withContext(Dispatchers.Main) {
                    Log.d("API_DEBUG", "Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {

                            if (data.data != null && data.data.isNotEmpty()) {
                                friendsList.clear()
                                friendsList.addAll(data.data)
                                adapter.updateList(friendsList)

                            } else {

                                if (data.message != null) {
                                    Toast.makeText(
                                        this@FriendsListActivity,
                                        data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@FriendsListActivity,
                                        "Bạn chưa có bạn bè nào",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@FriendsListActivity,
                                "Không có dữ liệu trả về",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Xử lý lỗi HTTP
                        handleApiError(response.code(), response.errorBody()?.string())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", "Exception: ${e.message}", e)
                    Toast.makeText(
                        this@FriendsListActivity,
                        "Lỗi kết nối: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleApiError(errorCode: Int, errorBody: String?) {
        Log.e("API_ERROR", "Error code: $errorCode")
        Log.e("API_ERROR", "Error body: $errorBody")

        val errorMessage = when (errorCode) {
            400 -> "Request không hợp lệ"
            401 -> {
                // Token hết hạn, chuyển về login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                "Phiên đăng nhập hết hạn"
            }
            403 -> {
                // Không có quyền truy cập
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                "Không có quyền truy cập. Vui lòng đăng nhập lại"
            }
            404 -> "API không tồn tại"
            500 -> "Lỗi server"
            else -> "Lỗi: $errorCode"
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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