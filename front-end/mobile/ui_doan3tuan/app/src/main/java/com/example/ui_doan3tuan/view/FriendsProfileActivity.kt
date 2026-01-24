package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.ApiClient
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterUserProfile
import com.example.ui_doan3tuan.adapter.PostAdapter
import com.example.ui_doan3tuan.model.*
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.getValue

class FriendsProfileActivity : AppCompatActivity() {

    private lateinit var btnKetBan: Button
    private lateinit var btnNhanTin: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: AdapterUserProfile
    private val viewModel2: NewsletterViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var revMessengerContacts: RecyclerView
    private lateinit var accessToken: String
    private var userId: Int = 0
    private var friendId: Int = -1
    private var friendName: String = ""
    private var isFriend: Boolean = false
    private var hasSentRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)
        sessionManager = SessionManager(applicationContext)
        val tokenFromSession = sessionManager.getAccessToken()
        if (tokenFromSession == null) {
            return
        }
        accessToken = tokenFromSession

        userId = sessionManager.getUser().id
        if (userId == -1) {
            return
        }
        getDataFromIntent()
        recyclerView = findViewById(R.id.rev_baidang)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = AdapterUserProfile(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) },
            onLikeClick = { post, isActionLike ->
                if (isActionLike) viewModel2.likePost(accessToken, post.id)
                else viewModel2.UnlikePost(accessToken, post.id)
            },
            onShareClick = { post ->
                showShareDialog(post)
            }
        )
        recyclerView.adapter = postAdapter
        findViews()
        showInfo()
        setupButtons()


        checkFriendStatusFromAPI()
    }

    private fun getDataFromIntent() {
        friendId = intent.getIntExtra("id", -1)
        Log.d("id", "friendId")
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
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_buttom_sheet_report_profile, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)

        btnReport.setOnClickListener {
//            viewModel.deletePost(post.id, accessToken)
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }
    private fun showShareDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_share, null)
        revMessengerContacts = view.findViewById(R.id.revMessengerContacts)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvTitleMessenger = view.findViewById<TextView>(R.id.tvTitleMessenger)
        val imgCurrentUserAvatar = view.findViewById<ImageView>(R.id.imgCurrentUserAvatar)
        val user = sessionManager.getUser()
        tvUserName.text = user.full_name
        if (user.avatar.toString().isNotEmpty()) {
            val fullUrl = "http://10.0.2.2:8989/api/images/${user.avatar}"
            imgCurrentUserAvatar.load(fullUrl) {
                crossfade(true)
                error(R.drawable.profile)
                placeholder(R.drawable.profile)
            }
        } else {
            imgCurrentUserAvatar.load(R.drawable.profile)
        }
        revMessengerContacts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val currentFriends = viewModel2.friends.value ?: mutableListOf()
        Log.d("currentFriends", "$currentFriends")
        revMessengerContacts.adapter = AdapterFriends(currentFriends)
        dialog.setContentView(view)
        dialog.show()
    }
    private fun showCommentDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_comment, null)

        val rcvComments = view.findViewById<RecyclerView>(R.id.rvListComments)
        val edtComment = view.findViewById<EditText>(R.id.edtCommentContent)
        val btnSend = view.findViewById<ImageView>(R.id.btnSendComment)
        var commentCount = findViewById<TextView>(R.id.txtSLBinhLuan_BaiDang)

        val commentAdapter = AdapterComment(emptyList())
        rcvComments.layoutManager = LinearLayoutManager(this)
        rcvComments.adapter = commentAdapter
        viewModel2.clearComments()
        viewModel2.comments.observe(this) { listComments ->
            commentAdapter.updateData(listComments ?: emptyList())
            if (!listComments.isNullOrEmpty()) {
                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }

        viewModel2.getCommentsByPostId(post.id, accessToken)

        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel2.sendComment(post.id, content, accessToken)
                var tmpCount: Int = post.commentCount + 1
                commentCount.text = tmpCount.toString()
                edtComment.setText("")
//                viewModel2.getCommentsByPostId(post.id, accessToken)
            }
        }

        dialog.setOnDismissListener {
            viewModel2.comments.removeObservers(this)
        }
        dialog.setContentView(view)
        dialog.show()
    }
}
