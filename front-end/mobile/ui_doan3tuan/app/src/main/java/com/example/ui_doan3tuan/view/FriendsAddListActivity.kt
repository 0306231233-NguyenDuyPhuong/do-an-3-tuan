package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.FriendRequestAdapter
import com.example.ui_doan3tuan.model.AcceptRequest
import com.example.ui_doan3tuan.model.FriendRequest
import com.example.ui_doan3tuan.model.FriendRequestResponse
import com.example.ui_doan3tuan.model.RejectRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsAddListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var adapter: FriendRequestAdapter
    private val requestList = mutableListOf<FriendRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_add_list)

        recyclerView = findViewById(R.id.rvAddFriends)
        editTextSearch = findViewById(R.id.etSearch)

        setupRecyclerView()

        loadFriendRequests()

        findViewById<ImageView>(R.id.imgThoatLF).setOnClickListener {
            finish()
        }
    }

    // Hàm cài đặt RecyclerView
    private fun setupRecyclerView() {
        // Tạo adapter với 2 nút xử lý
        adapter = FriendRequestAdapter(requestList,
            onAcceptClick = { request ->
                // Khi bấm nút "Chấp nhận"
                acceptRequest(request)
            },
            onRejectClick = { request ->
                // Khi bấm nút "Từ chối"
                rejectRequest(request)
            }
        )

        // Cài đặt layout và adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Hàm load danh sách lời mời từ API
    private fun loadFriendRequests() {
        // Lấy token từ bộ nhớ
        val token = getTokenFromSharedPreferences()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        ApiClient.apiService.getFriendRequests("Bearer $token")
            .enqueue(object : Callback<FriendRequestResponse> {

                override fun onResponse(
                    call: Call<FriendRequestResponse>,
                    response: Response<FriendRequestResponse>
                ) {
                    // Kiểm tra response
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data != null && data.data.isNotEmpty()) {
                            // Có dữ liệu, hiển thị lên màn hình
                            requestList.clear()
                            requestList.addAll(data.data)
                            adapter.notifyDataSetChanged()

                            // Hiển thị thông báo
                            Toast.makeText(
                                this@FriendsAddListActivity,
                                "Đã tải ${data.data.size} lời mời",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Không có lời mời nào
                            Toast.makeText(
                                this@FriendsAddListActivity,
                                "Không có lời mời kết bạn",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // API trả về lỗi
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Lỗi tải dữ liệu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.ui_doan3tuan.model.FriendRequestResponse>,
                    t: Throwable
                ) {
                    // Lỗi kết nối mạng
                    Toast.makeText(
                        this@FriendsAddListActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Hàm xử lý khi bấm nút "Chấp nhận"
    private fun acceptRequest(request: FriendRequest) {
        val token = getTokenFromSharedPreferences()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request body
        val acceptBody = AcceptRequest(from = request.sender.id)

        // Gọi API chấp nhận
        ApiClient.apiService.acceptRequest("Bearer $token", acceptBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Thành công
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Đã chấp nhận kết bạn với ${request.sender.full_name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Xóa khỏi danh sách
                        requestList.remove(request)
                        adapter.notifyDataSetChanged()
                    } else {
                        // Thất bại
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Lỗi: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@FriendsAddListActivity,
                        "Lỗi kết nối",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // Hàm xử lý khi bấm nút "Từ chối"
    private fun rejectRequest(request: FriendRequest) {
        val token = getTokenFromSharedPreferences()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request body
        val rejectBody = RejectRequest(from = request.sender.id)

        // Gọi API từ chối
        ApiClient.apiService.rejectRequest("Bearer $token", rejectBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Thành công
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Đã từ chối lời mời của ${request.sender.full_name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Xóa khỏi danh sách
                        requestList.remove(request)
                        adapter.notifyDataSetChanged()
                    } else {
                        // Thất bại
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Lỗi: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@FriendsAddListActivity,
                        "Lỗi kết nối",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // lấy token từ SharedPreferences
    private fun getTokenFromSharedPreferences(): String {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        return sharedPref.getString("access_token", "") ?: ""
    }
}