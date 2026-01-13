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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.adapter.AdapterUserProfile
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.UserProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue
var slbb:Int = 0;
var slbv:Int = 0;
class UserProfileActivity : AppCompatActivity() {
    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var adapterUserProfile: AdapterUserProfile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        findViewById<ImageView>(R.id.imgSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        findViewById<ImageView>(R.id.imgThoatHoSoNguoiDung).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.btnChinhSuaTrangCaNhan).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
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
            onReportClick = { post -> showReportDialog(post) }
        )
        revDSBaiDang.adapter = adapterUserProfile
        viewModel.postsId.observe(this) { listPostsId ->
            if (listPostsId != null) {
                adapterUserProfile.updateData(listPostsId)
                txtSoLuongBanBe.setText(slbb.toString())
                txtSoLuongBaiViet.setText(slbv.toString())

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
            val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

            if (error == "TOKEN_EXPIRED") {
                sharedPref.edit().remove("access_token").apply()
                sharedPref.edit().remove("refresh_token").apply()
                sharedPref.edit().remove("access_token_time").apply()
                sharedPref.edit().remove("refresh_token_time").apply()
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.getPostID(token,userId)
    }
    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_buttom_sheet_report_profile, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)
        btnReport.setOnClickListener {
            viewModel.deletePost(post.id, token)
            viewModel.deletePost.observe(this) { reported ->
                if (reported) {
                    Toast.makeText(this, "Xoá thành công!", Toast.LENGTH_SHORT).show()

                    viewModel.getPostID(token,userId)
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

        viewModel.comments.observe(this) { listComments ->
            if (!listComments.isNullOrEmpty()) {
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            } else {
                Log.d("test", "Rỗng")
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }
        viewModel.getCommentsByPostId(post.id, token)
        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel.sendComment(post.id, content, token)
                edtComment.setText("")
                viewModel.getCommentsByPostId(post.id, token)
            }
        }
        dialog.setOnDismissListener {
            viewModel.comments.removeObservers(this)
        }
        dialog.setContentView(view)
        dialog.show()
    }







}