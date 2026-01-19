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
import com.example.ui_doan3tuan.session.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class FriendsListActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var adapter: FriendsAdapter
    private val friendsList = mutableListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)
        recyclerView = findViewById(R.id.rvFriends)
        editTextSearch = findViewById(R.id.etSearch)
        sessionManager = SessionManager(applicationContext)

        setupRecyclerView()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_friend
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, NewsletterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                    return@setOnItemSelectedListener false
                }
                R.id.nav_friend -> {
                    return@setOnItemSelectedListener true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, CreatePostActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                    return@setOnItemSelectedListener false
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, NotificationActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                    return@setOnItemSelectedListener false
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
                    return@setOnItemSelectedListener false
                }
            }
            false
        }

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
    private fun logoutgoLogin() {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadFriends() {

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            logoutgoLogin()
            return
        }

        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            logoutgoLogin()
            return
        }

        val fullToken = "Bearer $token"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    ApiClient.apiService.getFriendList(fullToken).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (!data?.data.isNullOrEmpty()) {
                            friendsList.clear()
                            friendsList.addAll(data!!.data)
                            adapter.updateList(friendsList)
                        } else {
                            Toast.makeText(
                                this@FriendsListActivity,
                                "Bạn chưa có bạn bè nào",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        handleApiError(response.code())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@FriendsListActivity,
                        "Lỗi kết nối: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleApiError(errorCode: Int) {
        when (errorCode) {
            401, 403 -> {
                Toast.makeText(
                    this,
                    "Phiên đăng nhập đã hết hạn",
                    Toast.LENGTH_SHORT
                ).show()
                logoutgoLogin()
            }
        }
    }


}