package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterUserProfile
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

var slbb: Int = 0
var slbv: Int = 0

class UserProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var accessToken: String
    private var userId: Int = 0
    private lateinit var adapterUserProfile: AdapterUserProfile
    private val viewModel: UserProfileViewModel by viewModels()
    private val viewModel2: NewsletterViewModel by viewModels()
    private lateinit var imgAvatar: ImageView
    private lateinit var txtTenNguoiDung: TextView
    private lateinit var txtSoLuongBaiViet: TextView
    private lateinit var txtSoLuongBanBe: TextView
    private lateinit var imgAllPost: ImageView
    private lateinit var imgPostFav: ImageView
    private lateinit var imgPostSave: ImageView
    private lateinit var txtEmptyState: TextView
    private lateinit var revDSBaiDang: RecyclerView
    private lateinit var revMessengerContacts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNav: BottomNavigationView

    // Biến trạng thái: 0 = All Post, 2 = Saved Post
    private var interact: Int = 0
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("UserProfile", "Đã quay về từ Edit Profile")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        sessionManager = SessionManager(applicationContext)
        val tokenFromSession = sessionManager.getAccessToken()
        if (tokenFromSession == null) {
            goToLogin()
            return
        }
        accessToken = tokenFromSession

        userId = sessionManager.getUser().id
        if (userId == -1) {
            goToLogin()
            return
        }

        initViews()
        setupRecyclerView()
        setupListeners()
        setupObservers()
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        loadUserDataFromSession()
        loadPostData()
        bottomNav.menu.findItem(R.id.nav_profile).isChecked = true
    }

    private fun initViews() {
        imgAvatar = findViewById(R.id.imgUserProfile)
        txtTenNguoiDung = findViewById(R.id.txtTenNguoiDung)
        txtSoLuongBaiViet = findViewById(R.id.txtSoLuongBaiViet)
        txtSoLuongBanBe = findViewById(R.id.txtSoLuongBanBe)

        imgAllPost = findViewById(R.id.imgAllPost)
        imgPostFav = findViewById(R.id.imgPostFav)
        imgPostSave = findViewById(R.id.imgPostSave)
        txtEmptyState = findViewById(R.id.txtEmptyState)

        revDSBaiDang = findViewById(R.id.revDSBaiDang)
        progressBar = findViewById(R.id.progressBarLoadingProfile)
        bottomNav = findViewById(R.id.bottom_navigation)
    }

    private fun loadUserDataFromSession() {
        val user = sessionManager.getUser()
        txtTenNguoiDung.text = user.full_name ?: "Nguyen Duong"
        val avatarPath = user.avatar
        if (avatarPath.isNullOrEmpty()) {
            imgAvatar.load(R.drawable.profile)
        } else {
            val fullUrl = "http://10.0.2.2:8989/api/images/$avatarPath"
            imgAvatar.load(fullUrl) {
                placeholder(R.drawable.profile)
                error(R.drawable.profile)
                crossfade(true)
            }
        }
    }

    private fun loadPostData() {

        viewModel2.getListfriends(accessToken)
        if (interact == 0) {
            viewModel.getPostID(accessToken, userId)
        } else if (interact == 2) {
            viewModel.getListPostSave(accessToken)
        } else if (interact == 3) {
            viewModel.getListPostLike(accessToken)
        }
    }

    private fun setupRecyclerView() {
        revDSBaiDang.layoutManager = LinearLayoutManager(this)
        adapterUserProfile = AdapterUserProfile(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) },
            onLikeClick = { post, isActionLike ->
                if (isActionLike) viewModel2.likePost(accessToken, post.id)
                else viewModel2.UnlikePost(accessToken, post.id)
            },
            onShareClick = { post ->
                showShareDialog(post)
            },
            onImageClick = { id ->
                if(id.toInt()==userId){
                    return@AdapterUserProfile
                }
                val intent = Intent(this, FriendsProfileActivity::class.java)
                intent.putExtra("friend_id", id.toInt())
                startActivity(intent)
            },
        )
        revDSBaiDang.adapter = adapterUserProfile
    }

    private fun setupObservers() {
        viewModel.postsId.observe(this) { listPostsId ->
            if (listPostsId.isEmpty()) {
                txtEmptyState.visibility = View.VISIBLE
                revDSBaiDang.visibility = View.GONE
                txtEmptyState.text = "Không có bài viết nào!"
            } else {
                txtEmptyState.visibility = View.GONE
                revDSBaiDang.visibility = View.VISIBLE
                Log.d("UserProfile", "Data loaded: ${listPostsId.size} items")
                adapterUserProfile.setData(listPostsId)
            }
            slbv = listPostsId.size
            if (interact == 0) {
                txtSoLuongBaiViet.text = slbv.toString()
                txtSoLuongBanBe.text = slbb.toString()
            }

        }

        viewModel.deletePost.observe(this) { isDeleted ->
            if (isDeleted) {
                Toast.makeText(this, "Xoá thành công!", Toast.LENGTH_SHORT).show()
                loadPostData()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                revDSBaiDang.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                revDSBaiDang.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(this) { error ->
            if (error == "TOKEN_EXPIRED") {
                Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show()
                sessionManager.clearSession()
                goToLogin()
            }
        }
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.imgSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }


        findViewById<Button>(R.id.btnChinhSuaTrangCaNhan).setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }
        imgAllPost.setOnClickListener {
            interact = 0
            updateIconState(imgAllPost)
            adapterUserProfile.setData(emptyList())
            viewModel.getPostID(accessToken, userId)
        }
        imgPostFav.setOnClickListener {
            interact = 3
            updateIconState(imgPostFav)
            adapterUserProfile.setData(emptyList())
            viewModel.getListPostLike(accessToken)

        }
        imgPostSave.setOnClickListener {
            if (interact != 2) {
                interact = 2
                updateIconState(imgPostSave)
                adapterUserProfile.setData(emptyList())
                viewModel.getListPostSave(accessToken)
            }
        }

        // Set mặc định icon state
        updateIconState(imgAllPost)
    }

    private fun updateIconState(selectedIcon: ImageView) {
        val colorUnselected = getColor(R.color.gray)
        val colorSelected = getColor(R.color.white)
        imgAllPost.setColorFilter(colorUnselected)
        imgPostFav.setColorFilter(colorUnselected)
        imgPostSave.setColorFilter(colorUnselected)
        selectedIcon.setColorFilter(colorSelected)
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> navigateTo(NewsletterActivity::class.java)
                R.id.nav_friend -> navigateTo(FriendsListActivity::class.java)
                R.id.nav_add -> navigateTo(CreatePostActivity::class.java)
                R.id.nav_notification -> navigateTo(NotificationActivity::class.java)
                R.id.nav_profile -> true
                else -> false
            }
        }
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
        revMessengerContacts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val currentFriends = viewModel2.friends.value ?: mutableListOf()
        Log.d("currentFriends", "$currentFriends")
        revMessengerContacts.adapter = AdapterFriends(currentFriends) {
            val intent = Intent(this, FriendsProfileActivity::class.java)
            intent.putExtra("friend_id", it.id)
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun navigateTo(cls: Class<*>): Boolean {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
        return false
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
            viewModel.deletePost(post.id, accessToken)
            dialog.dismiss()
        }
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