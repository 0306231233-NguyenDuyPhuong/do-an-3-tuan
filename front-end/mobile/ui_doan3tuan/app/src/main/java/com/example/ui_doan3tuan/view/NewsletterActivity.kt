package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
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
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOjAsImlhdCI6MTc2ODAyMTg0NiwiZXhwIjoxNzY4MDI1NDQ2fQ.EP08znYmSDUKPBwa9C1HFGjTuVizrxF4FwUImH14tD8"
class NewsletterActivity : AppCompatActivity() {

    private val viewModel: NewsletterViewModel by viewModels()
    private lateinit var adapterNewsletter: AdapterNewsletter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)

        val revHienBaiDang = findViewById<RecyclerView>(R.id.revHienBaiDang)

        revHienBaiDang.layoutManager = LinearLayoutManager(this)
        adapterNewsletter = AdapterNewsletter(mutableListOf()){
        }
        revHienBaiDang.adapter = adapterNewsletter
        viewModel.posts.observe(this) { listPosts ->
            if (listPosts != null) {
                val newAdapter = AdapterNewsletter(listPosts){post ->
                    showCommentDialog(post)
                }
                revHienBaiDang.adapter = newAdapter
            }
        }
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
//        val token = sharedPref.getString("access_token", null)
        if (token != null) {
            viewModel.getProduct(token)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



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
//                rcvComments.scrollToPosition(listComments.size - 1)
            }else{
                Log.d("test", "Rỗng")
                commentAdapter.updateData(listComments)
//                rcvComments.scrollToPosition(listComments.size - 1)
            }
        }
        viewModel.getCommentsByPostId(post.id,token)

        btnSend.setOnClickListener {
            val content = edtComment.text.toString()
            if (content.isNotBlank()) {
                viewModel.sendComment(post.id, content, token)
                edtComment.setText("")
                viewModel.getCommentsByPostId(post.id,token)
            }
        }

//        dialog.setOnDismissListener {
//            viewModel.comments.removeObservers(this)
//        }

        dialog.setContentView(view)
        dialog.show()
    }
}
