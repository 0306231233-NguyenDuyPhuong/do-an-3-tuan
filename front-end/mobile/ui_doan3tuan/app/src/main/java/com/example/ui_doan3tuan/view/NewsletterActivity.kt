package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

val token =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOjAsImlhdCI6MTc2ODE0NzE0MiwiZXhwIjoxNzY4MTUwNzQyfQ.0tjAiUU0scX85INhQ6ajkcRDNG8QcqwffW64OhPzlSY"
class NewsletterActivity : AppCompatActivity() {

    private val viewModel: NewsletterViewModel by viewModels()
    private lateinit var adapterNewsletter: AdapterNewsletter
    private lateinit var adapterFriends: AdapterFriends

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

        revHienBaiDang.layoutManager = LinearLayoutManager(this)
        adapterNewsletter = AdapterNewsletter(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) }
        )

        revHienBaiDang.adapter = adapterNewsletter
        viewModel.posts.observe(this) { listPosts ->
            if (listPosts != null) {
                adapterNewsletter.updateData(listPosts)
            }
        }


        revDSBanBe.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapterFriends = AdapterFriends(mutableListOf())
        revDSBanBe.adapter = adapterFriends
        viewModel.friends.observe(this){
            listFriend->
            if(listFriend.isNotEmpty()){
                revDSBanBe.adapter= AdapterFriends(listFriend)

            }

        }

        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        val token = sharedPref.getString("access_token", null)
        if (token != null) {
            viewModel.getPost(token)
            viewModel.getListfriends(token)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }







    }

    private fun showReportDialog(post: PostModel) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_report, null)
        val btnReport = view.findViewById<LinearLayout>(R.id.btnReport)
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
