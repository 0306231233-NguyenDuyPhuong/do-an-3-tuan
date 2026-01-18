package com.example.ui_doan3tuan.view

import android.content.Context
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.adapter.AdapterUserProfile
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue
var slbb:Int = 0;
var slbv:Int = 0;
class UserProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var accessToken: String
    private var userId: Int = 0

    private val viewModel: UserProfileViewModel by viewModels()
    private val viewModel2: NewsletterViewModel by viewModels()
    private lateinit var adapterUserProfile: AdapterUserProfile
    private var interact: Int = 0;
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("Check", "Đã sửa xong, đang load lại data...")
            viewModel.getPostID(accessToken, userId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        val imgAllPost = findViewById<ImageView>(R.id.imgAllPost)
        val imgPostFav = findViewById<ImageView>(R.id.imgPostFav)
        val imgPostSave = findViewById<ImageView>(R.id.imgPostSave)

        sessionManager = SessionManager(applicationContext)
        val tokenFromSession = sessionManager.getAccessToken()
        if (tokenFromSession == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        accessToken = tokenFromSession

        userId = sessionManager.getUser()?.id ?: run {
            startActivity(Intent(this, LoginActivity::class.java));
            Log.d("Null", "Null id ")
            finish()
            return
        }

        findViewById<ImageView>(R.id.imgSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        findViewById<ImageView>(R.id.imgThoatHoSoNguoiDung).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnChinhSuaTrangCaNhan).setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }
        val txtSoLuongBaiViet = findViewById<TextView>(R.id.txtSoLuongBaiViet)
        val txtSoLuongBanBe = findViewById<TextView>(R.id.txtSoLuongBanBe)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, NewsletterActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener false
                }
                R.id.nav_friend -> {
                    val intent = Intent(this, FriendsListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener false
                }
                R.id.nav_add -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener false
                }
                R.id.nav_notification -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener false
                }
                R.id.nav_profile -> {
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        val revDSBaiDang = findViewById<RecyclerView>(R.id.revDSBaiDang)
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
                viewModel2.sharePost(accessToken, post.id)
                Toast.makeText(this, "Đang chia sẻ: ${post.content}", Toast.LENGTH_SHORT).show()
            }
        )
        var imgAvatar = findViewById<ImageView>(R.id.imgUserProfile)
        var txtTenNguoiDung =findViewById<TextView>(R.id.txtTenNguoiDung)
        revDSBaiDang.adapter = adapterUserProfile
        viewModel.postsId.observe(this) { listPostsId ->
            if (listPostsId != null) {
                adapterUserProfile.updateData(listPostsId)
                txtSoLuongBanBe.setText(slbb.toString())
                txtSoLuongBaiViet.setText(slbv.toString())
                if(listPostsId.isNotEmpty() && interact == 0){
                    if(listPostsId.get(0).User.avatar == null || listPostsId.get(0).User.avatar == ""){
                        imgAvatar.load(R.drawable.profile)
                    }else{
                        imgAvatar.load("http://10.0.2.2:8989/api/images/${listPostsId.get(0).User.avatar}")
                    }
                    txtTenNguoiDung.setText(listPostsId.get(0).User.full_name)
                }

            }else{

                Log.d("Lỗi", "Null")
            }
        }
        val progressBar2 =  findViewById<ProgressBar>(R.id.progressBarLoadingProfile)
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar2.visibility = View.VISIBLE
                revDSBaiDang.visibility = View.GONE
            } else {
                progressBar2.visibility = View.GONE
                revDSBaiDang.visibility = View.VISIBLE
            }
        }
        viewModel.error.observe(this) { error ->
            if (error == "TOKEN_EXPIRED") {
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        fun updateIconState(selectedIcon: ImageView) {
            val colorUnselected = getColor(R.color.gray)
            val colorSelected = getColor(R.color.white)

            imgAllPost.setColorFilter(colorUnselected)
            imgPostFav.setColorFilter(colorUnselected)
            imgPostSave.setColorFilter(colorUnselected)

            selectedIcon.setColorFilter(colorSelected)
        }
        imgAllPost.setOnClickListener {
            adapterUserProfile.setData(emptyList())
            updateIconState(imgAllPost)
            viewModel.getPostID(accessToken, userId)
            interact = 0
        }

        imgPostFav.setOnClickListener {
            updateIconState(imgPostFav)
//            viewModel.getListPostSave(token, userId)
            adapterUserProfile.setData(emptyList())
            Toast.makeText(this, "Đang tải bài viết đã thích...", Toast.LENGTH_SHORT).show()
        }

        imgPostSave.setOnClickListener {
            updateIconState(imgPostSave)
            adapterUserProfile.setData(emptyList())
            viewModel.getListPostSave(accessToken)
            interact = 2
            Toast.makeText(this, "Đang tải bài viết đã lưu...", Toast.LENGTH_SHORT).show()
        }
        updateIconState(imgAllPost)
        if(interact == 0){
            viewModel.getPostID(accessToken, userId)
        }

    }

    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_buttom_sheet_report_profile, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)
        btnReport.setOnClickListener {
            viewModel.deletePost(post.id, accessToken)
            viewModel.deletePost.observe(this) { reported ->
                if (reported) {
                    Toast.makeText(this, "Xoá thành công!", Toast.LENGTH_SHORT).show()

                    viewModel.getPostID(accessToken,userId)
                }else{
                    Log.d("Lỗi", "Null")
                }
            }
            Log.e("Lỗi", "Id lần 1: ${post.id}")
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

        val commentAdapter = AdapterComment(emptyList())
        rcvComments.layoutManager = LinearLayoutManager(this)
        rcvComments.adapter = commentAdapter

        viewModel2.comments.observe(this) { listComments ->
            if (!listComments.isNullOrEmpty()) {
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            } else {
                Log.d("test", "Rỗng")
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }
        viewModel2.getCommentsByPostId(post.id, accessToken)
        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel2.sendComment(post.id, content, accessToken)
                edtComment.setText("")
                viewModel2.getCommentsByPostId(post.id, accessToken)
            }
        }
        dialog.setOnDismissListener {
            viewModel2.comments.removeObservers(this)
        }
        dialog.setContentView(view)
        dialog.show()
    }







}