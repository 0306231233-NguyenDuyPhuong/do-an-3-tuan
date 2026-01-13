package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.ApiMessage
import com.example.ui_doan3tuan.model.FriendListResponse
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

    //LƯU TRẠNG THÁI KHI ACTIVITY BỊ DESTROY
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFriend", isFriend)
        outState.putBoolean("hasSentRequest", hasSentRequest)
        outState.putInt("friendId", friendId)
        outState.putString("friendName", friendName)
    }

    //  KHÔI PHỤC TRẠNG THÁI KHI ACTIVITY TẠO LẠI
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


        if (!isFriend && !hasSentRequest) {
            checkFriendStatusFromAPI()
        }
    }


    private fun getDataFromIntent() {
        friendId = intent.getIntExtra("friend_id", -1)
        friendName = intent.getStringExtra("friend_name") ?: "Chưa có tên"

        //  isFriend = true nếu từ danh sách bạn bè VÀ chưa có trạng thái lưu
        if (!isFriend) {
            val fromFriendsList = intent.getBooleanExtra("from_friends_list", false)
            isFriend = fromFriendsList
        }
    }


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
                        val friendList = response.body()?.data
                        if (friendList != null) {
                            val isActuallyFriend = friendList.any { it.id == friendId }

                            if (isActuallyFriend != isFriend) {
                                isFriend = isActuallyFriend
                                updateButtonUI()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<FriendListResponse>, t: Throwable) {}
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

        // Hiển thị ảnh (chỉ lấy từ intent khi vào lần đầu)
        val imgAvatar = findViewById<ImageView>(R.id.imageView9)
        val friendAvatar = intent.getStringExtra("friend_avatar")

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
                // Mở chat
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("friend_id", friendId)
                intent.putExtra("friend_name", friendName)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cần kết bạn trước khi nhắn tin", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút kết bạn
        btnKetBan.setOnClickListener {
            if (isFriend) {
                // Đã là bạn bè -> hủy kết bạn
                showUnfriendDialog()
            } else if (hasSentRequest) {
                // Đã gửi lời mời -> thông báo
                Toast.makeText(this, "Đã gửi lời mời, đang chờ phản hồi", Toast.LENGTH_SHORT).show()
            } else {
                // Chưa là bạn bè -> gửi lời mời
                sendFriendRequest()
            }
        }

        // Nút thoát
        findViewById<ImageView>(R.id.imgThoatFP).setOnClickListener {
            finish()
        }
    }

    private fun updateButtonUI() {
        if (isFriend) {
            // TRẠNG THÁI 1: ĐÃ LÀ BẠN BÈ
            btnKetBan.text = "Bạn bè"
            btnKetBan.setBackgroundColor(getColor(android.R.color.darker_gray))
            btnKetBan.isEnabled = true
            btnNhanTin.isEnabled = true
        } else if (hasSentRequest) {
            // TRẠNG THÁI 2: ĐÃ GỬI LỜI MỜI
            btnKetBan.text = "Đã gửi lời mời"
            btnKetBan.setBackgroundColor(getColor(android.R.color.darker_gray))
            btnKetBan.isEnabled = false
            btnNhanTin.isEnabled = false
        } else {
            // TRẠNG THÁI 3: CHƯA LÀ BẠN BÈ
            btnKetBan.text = "Kết bạn"
            btnKetBan.setBackgroundColor(getColor(R.color.background_btn))
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
                    Toast.makeText(this@FriendsProfileActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                    btnKetBan.text = "Kết bạn"
                    btnKetBan.isEnabled = true
                }
            })
    }
    private fun handleSendError(errorCode: Int) {
        val message = when (errorCode) {
            400 -> {
                // Có thể đã là bạn bè, kiểm tra lại
                checkFriendStatusFromAPI()
                "Đã gửi lời mời trước đó"
            }
            403 -> "Bị chặn kết bạn"
            404 -> "Không tìm thấy người dùng"
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
            .setMessage("Bạn có chắc muốn hủy kết bạn với $friendName?")
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

        val request = UnfriendRequest(friendId = friendId)

        ApiClient.apiService.unfriend("Bearer $token", request)
            .enqueue(object : Callback<ApiMessage> {
                override fun onResponse(
                    call: Call<ApiMessage>,
                    response: Response<ApiMessage>
                ) {
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
                        Toast.makeText(
                            this@FriendsProfileActivity,
                            "Lỗi: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiMessage>, t: Throwable) {
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