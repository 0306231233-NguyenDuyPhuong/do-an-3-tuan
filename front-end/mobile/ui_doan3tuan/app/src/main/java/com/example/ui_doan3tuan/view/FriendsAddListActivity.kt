package com.example.ui_doan3tuan.view

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
import com.example.ui_doan3tuan.adapter.FriendRequestAdapter
import com.example.ui_doan3tuan.model.AcceptRequest
import com.example.ui_doan3tuan.model.FriendRequest
import com.example.ui_doan3tuan.model.FriendRequestResponse
import com.example.ui_doan3tuan.model.RejectRequest
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsAddListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextSearch: EditText
    private lateinit var imgBack: ImageView

    private lateinit var adapter: FriendRequestAdapter
    private val requestList = mutableListOf<FriendRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_add_list)
        recyclerView = findViewById(R.id.rvAddFriends)
        editTextSearch = findViewById(R.id.etSearch)
        imgBack = findViewById(R.id.imgThoatLF)

        setupRecyclerView()
        loadFriendRequests()

        imgBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
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
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }


    private fun loadFriendRequests() {
        // Lấy token đăng nhập
        val token = getToken()

        // Kiểm tra xem đã đăng nhập chưa
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

                ) {  Log.e("ACCEPT_API", "Response code: ${response.code()}")
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data != null && data.data.isNotEmpty()) {
                            requestList.clear()

                            // 2. Thêm dữ liệu mới
                            requestList.addAll(data.data)


                            adapter.notifyDataSetChanged()

                            // 4. Hiển thị thông báo
                            Toast.makeText(
                                this@FriendsAddListActivity,
                                "Đã tải lời mời",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // KHÔNG CÓ DỮ LIỆU
                            Toast.makeText(
                                this@FriendsAddListActivity,
                                "Không có lời mời kết bạn",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // API TRẢ VỀ LỖI
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Lỗi tải dữ liệu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FriendRequestResponse>, t: Throwable) {
                    // LỖI KẾT NỐI MẠNG
                    Toast.makeText(
                        this@FriendsAddListActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // HÀM XỬ LÝ KHI CHẤP NHẬN LỜI MỜI
    private fun acceptRequest(request: FriendRequest) {
        val token = getToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request body
        val acceptBody = AcceptRequest(from = request.sender.id)
        Log.e("ACCEPT_API", "TOKEN: Bearer $token")
        Log.e("ACCEPT_API", "BODY: ${Gson().toJson(acceptBody)}")
        ApiClient.apiService.acceptRequest("Bearer $token", acceptBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // THÀNH CÔNG
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Đã chấp nhận kết bạn ",
                            Toast.LENGTH_SHORT

                        ).show()
                        requestList.remove(request)
                        adapter.notifyDataSetChanged()
                    } else {
                        // THẤT BẠI
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

    // HÀM XỬ LÝ KHI TỪ CHỐI LỜI MỜI
    private fun rejectRequest(request: FriendRequest) {
        val token = getToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo request body (gửi id người gửi lời mời)
        val rejectBody = RejectRequest(from = request.sender.id)
        Log.e("REJECT_API", "BODY: ${Gson().toJson(rejectBody)}")
        Log.e("REJECT_API", "TOKEN: Bearer $token")
        // GỌI API TỪ CHỐI
        ApiClient.apiService.rejectRequest("Bearer $token", rejectBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // THÀNH CÔNG
                        Toast.makeText(
                            this@FriendsAddListActivity,
                            "Đã từ chối lời mời của ${request.sender.full_name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // XÓA LỜI MỜI KHỎI DANH SÁCH
                        requestList.remove(request)
                        adapter.notifyDataSetChanged()
                    } else {
                        // THẤT BẠI
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
    private fun getToken(): String {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        return sharedPref.getString("access_token", "") ?: ""
    }
}
