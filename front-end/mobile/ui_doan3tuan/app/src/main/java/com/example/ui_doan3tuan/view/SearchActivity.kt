package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.SearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapterNewsletter: AdapterNewsletter
    var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        findViewById<ImageView>(R.id.btnBackSearch).setOnClickListener {
            page = 1;
            finish()
        }
        val revRecentSearch = findViewById<RecyclerView>(R.id.revRecentSearch)
        revRecentSearch.layoutManager = LinearLayoutManager(this)


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
        revRecentSearch.adapter = adapterNewsletter
        viewModel.resultSearch.observe(this) { listPosts ->
            if (listPosts != null) {
                adapterNewsletter.updateData(listPosts)
            }
        }
        findViewById<EditText>(R.id.edtSearch).setOnEditorActionListener { v, actionId, event ->
            // Kiểm tra xem người dùng có nhấn nút Search trên bàn phím không
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = findViewById<EditText>(R.id.edtSearch).text.toString()
                val nestedScrollView2 = findViewById<NestedScrollView>(R.id.myNested)
                val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                val token = sharedPref.getString("access_token", null)
                if (token != null) {
                    viewModel.searchContent(token,page,query)
                    viewModel.getListfriends(token)
                    nestedScrollView2.setOnScrollChangeListener(
                        NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                            if (!v.canScrollVertically(1)) {
                                Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT)
                                    .show()
                                page += 1;
                                viewModel.searchContent(token, page,query)

                            }
                        }
                    )
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                true
            } else {
                false
            }
        }






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

        viewModel.comments.observe(this) { listComments ->
            if (!listComments.isNullOrEmpty()) {
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            } else {
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