package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.FriendRequestAdapter
import com.example.ui_doan3tuan.model.ApiMessage
import com.example.ui_doan3tuan.model.FriendListResponse
import com.example.ui_doan3tuan.model.FriendRequest
import com.example.ui_doan3tuan.model.SendRequest
import com.example.ui_doan3tuan.model.UnfriendRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsProfileActivity : AppCompatActivity() {


    private lateinit var btnKetBan: Button
    private lateinit var btnNhanTin: Button
    private var friendId: Int = -1
    private var friendName: String = ""
    private var isFriend: Boolean = false
    private var hasSentRequest: Boolean = false

    // LƯU TRẠNG THÁI KHI ACTIVITY BỊ DESTROY
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFriend", isFriend)
        outState.putBoolean("hasSentRequest", hasSentRequest)
        outState.putInt("friendId", friendId)
        outState.putString("friendName", friendName)
    }

    // KHÔI PHỤC TRẠNG THÁI KHI ACTIVITY TẠO LẠI
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isFriend = savedInstanceState.getBoolean("isFriend", false)
        hasSentRequest = savedInstanceState.getBoolean("hasSentRequest", false)
        friendId = savedInstanceState.getInt("friendId", -1)
        friendName = savedInstanceState.getString("friendName", "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        if (savedInstanceState != null) {
            isFriend = savedInstanceState.getBoolean("isFriend", false)
            hasSentRequest = savedInstanceState.getBoolean("hasSentRequest", false)
            friendId = savedInstanceState.getInt("friendId", -1)
            friendName = savedInstanceState.getString("friendName", "")
        }

        if (friendId == -1) {
            getDataFromIntent()
        }

        findViews()
        showInfo()
        setupButtons()

        // KIỂM TRA TRẠNG THÁI KẾT BẠN TỪ API
        checkFriendStatusFromAPI()
    }

    private fun getDataFromIntent() {
        friendId = intent.getIntExtra("friend_id", -1)
        friendName = intent.getStringExtra("friend_name") ?: "Chưa có tên"

        // isFriend = true nếu từ danh sách bạn bè VÀ chưa có trạng thái lưu
        if (!isFriend) {
            val fromFriendsList = intent.getBooleanExtra("from_friends_list", false)
            isFriend = fromFriendsList
        }
    }

    private fun checkFriendStatusFromAPI() {
        val token = getToken()
        if (token.isEmpty()) {
            Log.d("FriendsProfile", "Token is empty, cannot check friend status")
            return
        }

        Log.d("FriendsProfile", "Checking friend status for friendId: $friendId")

        ApiClient.apiService.getFriendList("Bearer $token")
            .enqueue(object : Callback<FriendListResponse> {
                override fun onResponse(
                    call: Call<FriendListResponse>,
                    response: Response<FriendListResponse>
                ) {
                    if (response.isSuccessful) {
                        val friendList = response.body()?.data
                        if (friendList != null) {
                            // KIỂM TRA XEM friendId CÓ TRONG DANH SÁCH BẠN BÈ KHÔNG
                            val isActuallyFriend = friendList.any { it.id == friendId }

                            Log.d("FriendsProfile", "Friend list: $friendList")
                            Log.d("FriendsProfile", "isActuallyFriend: $isActuallyFriend, current isFriend: $isFriend")

                            if (isActuallyFriend != isFriend) {
                                isFriend = isActuallyFriend
                                updateButtonUI()
                            }

                            // NẾU LÀ BẠN BÈ THÌ KHÔNG THỂ CÓ LỜI MỜI ĐÃ GỬI
                            if (isFriend) {
                                hasSentRequest = false
                            }
                        }
                    } else {
                        Log.e("FriendsProfile", "Failed to get friend list: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<FriendListResponse>, t: Throwable) {
                    Log.e("FriendsProfile", "Network error checking friend status", t)
                }
            })
    }

    private fun findViews() {
        btnKetBan = findViewById(R.id.btnKetBan)
        btnNhanTin = findViewById(R.id.btnNhanTin)
    }

    private fun showInfo() {
        // Hiển thị tên
        val txtName = findViewById<TextView>(R.id.textView9)
        txtName.text = friendName

        // Hiển thị ảnh
        val imgAvatar = findViewById<ImageView>(R.id.imageView9)
        if (!friendAvatar.isNullOrEmpty()) {
            Glide.with(this)
                .load(friendAvatar)
                .placeholder(R.drawable.profile)
                .into(imgAvatar)
        }
    }

    private fun setupButtons() {
        // Cập nhật giao diện nút
        updateButtonUI()

        // Nút nhắn tin
        btnNhanTin.setOnClickListener {
            if (isFriend) {
                // Mở chat nếu là bạn bè
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("friend_id", friendId)
                intent.putExtra("friend_name", friendName)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cần kết bạn trước khi nhắn tin", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút kết bạn/hủy kết bạn
        btnKetBan.setOnClickListener {
            if (isFriend) {
                // ĐÃ LÀ BẠN BÈ -> HỦY KẾT BẠN
                showUnfriendDialog()
            } else if (hasSentRequest) {
                // ĐÃ GỬI LỜI MỜI -> THÔNG BÁO
                Toast.makeText(this, "Đã gửi lời mời, đang chờ phản hồi", Toast.LENGTH_SHORT).show()
            } else {
                // CHƯA LÀ BẠN BÈ -> GỬI LỜI MỜI
                sendFriendRequest()
            }
        }

        // Nút thoát
        findViewById<ImageView>(R.id.imgThoatFP).setOnClickListener {
            finish()
        }
    }

    private fun updateButtonUI() {
        Log.d("FriendsProfile", "Updating button UI - isFriend: $isFriend, hasSentRequest: $hasSentRequest")

        if (isFriend) {
            // TRẠNG THÁI 1: ĐÃ LÀ BẠN BÈ
            btnKetBan.text = "Bạn bè"
            btnKetBan.isEnabled = true
            btnNhanTin.isEnabled = true
        } else if (hasSentRequest) {
            // TRẠNG THÁI 2: ĐÃ GỬI LỜI MỜI
            btnKetBan.text = "Đã gửi lời mời"
            btnKetBan.isEnabled = false  // Không cho click
            btnNhanTin.isEnabled = false
        } else {
            // TRẠNG THÁI 3: CHƯA LÀ BẠN BÈ
            btnKetBan.text = "Kết bạn"
            btnKetBan.isEnabled = true
            btnNhanTin.isEnabled = false
        }
    }

    private fun sendFriendRequest() {
        val token = getToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("FriendsProfile", "Sending friend request to: $friendId")

        // Hiển thị loading
        btnKetBan.text = "Đang gửi..."
        btnKetBan.isEnabled = false

        val request = SendRequest(to = friendId)

        ApiClient.apiService.sendFriendRequest("Bearer $token", request)
            .enqueue(object : Callback<ApiMessage> {
                override fun onResponse(
                    call: Call<ApiMessage>,
                    response: Response<ApiMessage>
                ) {
                    Log.d("FriendsProfile", "Send request response: ${response.code()}")

                    if (response.isSuccessful) {
                        // Thành công
                        Toast.makeText(
                            this@FriendsProfileActivity,
                            "Đã gửi lời mời kết bạn",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Cập nhật trạng thái
                        hasSentRequest = true
                        updateButtonUI()
                    } else {
                        // Xử lý lỗi
                        handleSendError(response.code())
                        updateButtonUI()
                    }
                }

                override fun onFailure(call: Call<ApiMessage>, t: Throwable) {
                    Log.e("FriendsProfile", "Send request failed", t)
                    Toast.makeText(this@FriendsProfileActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                    btnKetBan.text = "Kết bạn"
                    btnKetBan.isEnabled = true
                }
            })
    }

    private fun handleSendError(errorCode: Int) {
        Log.e("FriendsProfile", "Send request error: $errorCode")

        val message = when (errorCode) {
            400 -> {
                checkFriendStatusFromAPI()
                "Đã gửi lời mời trước đó"
            }
            403 -> "Bị chặn kết bạn"
            404 -> "Không tìm thấy người dùng"
            409 -> {
                // Đã là bạn bè
                checkFriendStatusFromAPI()
                "Đã là bạn bè"
            }
            500 -> "Lỗi server"
            else -> "Lỗi: $errorCode"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Khôi phục nút
        btnKetBan.text = "Kết bạn"
        btnKetBan.isEnabled = true
    }

    private fun showUnfriendDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hủy kết bạn")
            .setMessage("Bạn có chắc muốn hủy kết bạn ")
            .setPositiveButton("Có") { dialog, which ->
                unfriend()
            }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun unfriend() {
        val token = getToken()
        if (token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("FriendsProfile", "Unfriend: friendId = $friendId")

        val request = UnfriendRequest(friendId = friendId)

        ApiClient.apiService.unfriend("Bearer $token", request)
            .enqueue(object : Callback<ApiMessage> {
                override fun onResponse(
                    call: Call<ApiMessage>,
                    response: Response<ApiMessage>
                ) {
                    Log.d("FriendsProfile", "Unfriend response: ${response.code()}")

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FriendsProfileActivity,
                            "Đã hủy kết bạn",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Cập nhật trạng thái
                        isFriend = false
                        hasSentRequest = false
                        updateButtonUI()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("FriendsProfile", "Unfriend error ${response.code()}: $errorBody")

                        Toast.makeText(
                            this@FriendsProfileActivity,
                            "Lỗi: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiMessage>, t: Throwable) {
                    Log.e("FriendsProfile", "Unfriend network error", t)
                    Toast.makeText(this@FriendsProfileActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getToken(): String {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        return sharedPref.getString("access_token", "") ?: ""
    }

    override fun onResume() {
        super.onResume()

        checkFriendStatusFromAPI()
    }
}