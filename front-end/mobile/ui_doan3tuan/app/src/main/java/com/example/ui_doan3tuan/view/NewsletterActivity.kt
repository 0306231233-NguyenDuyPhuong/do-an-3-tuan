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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

var userId: Int = 1;

class NewsletterActivity : AppCompatActivity() {

    private val viewModel: NewsletterViewModel by viewModels()
    private lateinit var adapterNewsletter: AdapterNewsletter
    private lateinit var adapterFriends: AdapterFriends
    var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    return@setOnItemSelectedListener true
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
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener false
                }
            }
            false
        }

        val revHienBaiDang = findViewById<RecyclerView>(R.id.revHienBaiDang)
        val revDSBanBe = findViewById<RecyclerView>(R.id.revDSBanBe)
        val nestedScrollView = findViewById<NestedScrollView>(R.id.myNestedScrollView)

        revHienBaiDang.layoutManager = LinearLayoutManager(this)
        adapterNewsletter = AdapterNewsletter(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) },
            onImageClick = { id ->
                val intent = Intent(this, FriendsProfileActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            },
            onLikeClick = { post -> viewModel.isLiked(token, post.id) }
        )

        revHienBaiDang.adapter = adapterNewsletter
        viewModel.posts.observe(this) { listPosts ->
            if (listPosts != null) {
                adapterNewsletter.updateData(listPosts)
            }
        }
<<<<<<< HEAD
        val progressBar =  findViewById<ProgressBar>(R.id.progressBar)
=======
        val progressBar =  findViewById<ProgressBar>(R.id.progressBarLoadingNewletter)
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                revHienBaiDang.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                revHienBaiDang.visibility = View.VISIBLE
            }
        }
        viewModel.error.observe(this) { error ->
            val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

            if (error == "TOKEN_EXPIRED") {
                sharedPref.edit().remove("access_token").apply()
                sharedPref.edit().remove("refresh_token").apply()
<<<<<<< HEAD
=======
                sharedPref.edit().remove("access_token_time").apply()
                sharedPref.edit().remove("refresh_token_time").apply()
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
<<<<<<< HEAD
        Log.d("token", "$token")
=======

>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2





        revDSBanBe.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterFriends = AdapterFriends(mutableListOf())
        revDSBanBe.adapter = adapterFriends
        viewModel.friends.observe(this) { listFriend ->
            if (listFriend.isNotEmpty()) {
                revDSBanBe.adapter = AdapterFriends(listFriend)

            }

        }
<<<<<<< HEAD
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val token = sharedPref.getString("access_token", null)
        if (token != null) {
            viewModel.getPost(token,page)
            viewModel.getListfriends(token)
            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                    if (!v.canScrollVertically(1)) {
                        Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT)
                            .show()
                        page += 1;
                        viewModel.getPost(token, page)


                    }
                }
            )
        } else {
            val intent = Intent(this, LoginActivity::class.java)
=======

        viewModel.getPost(token,page)
        viewModel.getListfriends(token)
        nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                if (!v.canScrollVertically(1)) {
                    Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT)
                        .show()
                    page += 1;
                    viewModel.getPost(token, page)


                }
            }
        )
        findViewById<ImageView>(R.id.imgSearch).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2
            startActivity(intent)


        }
<<<<<<< HEAD
        findViewById<ImageView>(R.id.imgSearch).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)


        }
=======
>>>>>>> 0705643a61e733822215bae2f9688a863fa0d6c2


    }


    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_report, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)
        btnReport.setOnClickListener {
            showDetailReportDialog(post.User.id, post.id)

            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showDetailReportDialog(id: Int, postId: Int) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_buttom_sheet_detail_report, null)
        val txtSpam = view.findViewById<TextView>(R.id.txtSpam)
        val txtThongTinSai = view.findViewById<TextView>(R.id.txtThongTinSai)
        val txtVandenhaycam = view.findViewById<TextView>(R.id.txtVandenhaycam)
        txtSpam.setOnClickListener {
            viewModel.reportPost(token, postId, id, "Spam")
            Toast.makeText(this, "Báo cáo thành công spam!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        txtThongTinSai.setOnClickListener {
            viewModel.reportPost(token, postId, id, "Thông tin sai sự thật")
            Toast.makeText(this, "Báo cáo thành công thông tin sai!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        txtVandenhaycam.setOnClickListener {
            viewModel.reportPost(token, postId, id, "Nội dung nhạy cảm")
            Toast.makeText(this, "Báo cáo thành công vấn đề nhạy cảm!", Toast.LENGTH_SHORT).show()
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
        viewModel.clearComments()
        viewModel.comments.observe(this) { listComments ->
            Log.d("listComments", "$listComments")
            Log.d("CheckComment", "Số lượng comment: ${listComments.size}")
            commentAdapter.updateData(listComments)
            if (listComments.isNotEmpty()) {
                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }
        viewModel.getCommentsByPostId(post.id, token)
        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel.sendComment(post.id, content, token)
                edtComment.setText("")
//                viewModel.getCommentsByPostId(post.id, token)
            }
        }
        dialog.setOnDismissListener {
            viewModel.comments.removeObservers(this)
        }
        dialog.setContentView(view)
        dialog.show()
    }
}