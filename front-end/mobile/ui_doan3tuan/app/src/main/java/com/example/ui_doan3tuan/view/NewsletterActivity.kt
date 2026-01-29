package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.view.ConversationActivity
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.model.User
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView


class NewsletterActivity : AppCompatActivity() {


    private lateinit var adapterNewsletter: AdapterNewsletter
    private lateinit var adapterFriends: AdapterFriends
    private lateinit var imgChat: ImageView
    private lateinit var sessionManager: SessionManager
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var revHienBaiDang: RecyclerView
    private lateinit var revDSBanBe: RecyclerView
    private lateinit var revMessengerContacts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var imgSearch: ImageView

    private val viewModel: NewsletterViewModel by viewModels()

    private var token: String? = ""
    private var page: Int = 1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)

        sessionManager = SessionManager(applicationContext)
        token = sessionManager.getAccessToken()
        val user = sessionManager.getUser()
        userId = user.id


        initViews()
        setupListeners()
        setupRecyclerView()
        setupObservers()
        setupBottomNav()
        // Xử lý khi đang ở Home mà bấm lại icon Home (Refresh)
        bottomNav.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                Toast.makeText(this, "Đang làm mới...", Toast.LENGTH_SHORT).show()
                loadFreshData(nestedScrollView)
            }
        }

        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, _, _, _ ->
            if (!v.canScrollVertically(1)) {
                Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT).show()
                page += 1
                viewModel.getPost(token!!, page)
            }
        }
//        loadPostData()


    }
    override fun onResume(){
        super.onResume()
//        loadFreshData(nestedScrollView)
        loadPostData()
        bottomNav.menu.findItem(R.id.nav_home).isChecked = true
    }
    private fun loadPostData(){
        if (token!!.isNotEmpty()) {
            viewModel.getPost(token!!, page)
            viewModel.getListfriends(token!!)
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy Token đăng nhập", Toast.LENGTH_SHORT).show()
        }
    }
    private fun initViews(){
        bottomNav = findViewById(R.id.bottom_navigation)
        nestedScrollView = findViewById(R.id.myNestedScrollView)
        revHienBaiDang = findViewById(R.id.revHienBaiDang)
        revDSBanBe = findViewById(R.id.revDSBanBe)
        progressBar = findViewById(R.id.progressBarLoadingNewletter)
        imgChat = findViewById(R.id.imgChat)
        imgSearch = findViewById(R.id.imgSearch)

    }
    private fun setupObservers(){
        viewModel.posts.observe(this) { listPosts ->
            if (listPosts != null) {
                if (page == 1) {
                    adapterNewsletter.setData(listPosts)
                } else {
                    adapterNewsletter.updateData(listPosts)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading && page == 1) {
                progressBar.visibility = View.VISIBLE
                revHienBaiDang.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                revHienBaiDang.visibility = View.VISIBLE
            }
        }

        viewModel.friends.observe(this) { listFriend ->
            if (listFriend.isNotEmpty()) {
                val newAdapter = AdapterFriends(listFriend)
                revDSBanBe.adapter = newAdapter
            }
        }

        viewModel.error.observe(this) { error ->
            if (error == "TOKEN_EXPIRED") {
                sessionManager.clearSession()
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
    private fun setupListeners(){
        imgChat.setOnClickListener {
            val intent = Intent(this, ConversationActivity::class.java)
            startActivity(intent)
        }
        imgSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_friend -> navigateTo(FriendsListActivity::class.java)
                R.id.nav_add -> navigateTo(CreatePostActivity::class.java)
                R.id.nav_notification -> navigateTo(NotificationActivity::class.java)
                R.id.nav_profile -> navigateTo(UserProfileActivity::class.java)
                else -> false
            }
        }
    }
    private fun setupRecyclerView(){
        revHienBaiDang.layoutManager = LinearLayoutManager(this)
        adapterNewsletter = AdapterNewsletter(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) },
            onImageClick = { id ->
                if(id.toInt()==userId){
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    return@AdapterNewsletter
                }
                val intent = Intent(this, FriendsProfileActivity::class.java)
                intent.putExtra("friend_id", id.toInt())
                startActivity(intent)
            },
            onLikeClick = { post, isActionLike ->
                if (isActionLike) viewModel.likePost(token!!, post.id)
                else viewModel.UnlikePost(token!!, post.id)
            },
            onShareClick = { post ->
                showShareDialog(post)
//                viewModel.sharePost(token!!, post.id)
//                Toast.makeText(this, "Đang chia sẻ: ${post.content}", Toast.LENGTH_SHORT).show()
            }
        )
        revHienBaiDang.adapter = adapterNewsletter
        revDSBanBe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterFriends = AdapterFriends(mutableListOf())
        revDSBanBe.adapter = adapterFriends
    }
    private fun navigateTo(cls: Class<*>) : Boolean {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
        return false
    }
    private fun loadFreshData(scrollView: NestedScrollView) {
        // Cuộn lên đầu
        scrollView.smoothScrollTo(0, 0)
        //Xóa dữ liệu cũ trong ViewModel (Màn hình sẽ trắng/loading)
        viewModel.clearPosts()
        page = 1
        viewModel.getPost(token!!, page)
        viewModel.getListfriends(token!!)
    }
    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_report, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)
        val btnSavePost = view.findViewById<LinearLayout>(R.id.btnSavePost)

        if(post.User.id == userId) {
            btnReport.visibility = View.GONE
        }else{
            btnReport.visibility = View.VISIBLE
            btnReport.setOnClickListener {
                showDetailReportDialog(post.User.id, post.id)
            }
            dialog.dismiss()
        }

        btnSavePost.setOnClickListener {
            viewModel.savePost(token!!, post.id)
            Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show()
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
        val currentFriends = viewModel.friends.value ?: mutableListOf()
        revMessengerContacts.adapter = AdapterFriends(currentFriends)
        dialog.setContentView(view)
        dialog.show()
    }
    private fun showDetailReportDialog(id: Int, postId: Int) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_buttom_sheet_detail_report, null)
        val txtSpam = view.findViewById<TextView>(R.id.txtSpam)
        val txtThongTinSai = view.findViewById<TextView>(R.id.txtThongTinSai)
        val txtVandenhaycam = view.findViewById<TextView>(R.id.txtVandenhaycam)

        fun sendReport(reason: String) {
            viewModel.reportPost(token!!, postId, id, reason)
            Toast.makeText(this, "Đã báo cáo: $reason", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        txtSpam.setOnClickListener { sendReport("Spam") }
        txtThongTinSai.setOnClickListener { sendReport("Thông tin sai sự thật") }
        txtVandenhaycam.setOnClickListener { sendReport("Nội dung nhạy cảm") }

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

        viewModel.clearComments()

        viewModel.comments.observe(this) { listComments ->
            commentAdapter.updateData(listComments)
            if (listComments.isNotEmpty()) {
                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }

        viewModel.getCommentsByPostId(post.id, token!!)

        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel.sendComment(post.id, content, token!!)
                var tmpCount: Int = post.commentCount + 1
                commentCount.text = tmpCount.toString()
                edtComment.setText("")
            }
        }

        dialog.setOnDismissListener {
            viewModel.comments.removeObservers(this)
        }
        dialog.setContentView(view)
        dialog.show()
    }
}