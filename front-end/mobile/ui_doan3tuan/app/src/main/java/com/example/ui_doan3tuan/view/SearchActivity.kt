package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterComment
import com.example.ui_doan3tuan.adapter.AdapterFriends
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import com.example.ui_doan3tuan.viewmodel.SearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var accessToken: String
    private val viewModel: SearchViewModel by viewModels()
    private val viewModel2: NewsletterViewModel by viewModels()
    private lateinit var adapterNewsletter: AdapterNewsletter
    var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        sessionManager = SessionManager(applicationContext)

        val tokenFromSession = sessionManager.getAccessToken()
        if (tokenFromSession == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        accessToken = tokenFromSession

        findViewById<ImageView>(R.id.btnBackSearch).setOnClickListener {
            page = 1;
            finish()
        }
        val revRecentSearch = findViewById<RecyclerView>(R.id.revRecentSearch)
        revRecentSearch.layoutManager = LinearLayoutManager(this)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLoadingSearch)

        adapterNewsletter = AdapterNewsletter(
            mutableListOf(),
            onCommentClick = { post -> showCommentDialog(post) },
            onReportClick = { post -> showReportDialog(post) },
            onImageClick = { id ->
                val intent = Intent(this, FriendsProfileActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            },
            onLikeClick = { post, isActionLike ->
                if (isActionLike) {
                    viewModel2.likePost(accessToken, post.id)
                } else {
                    viewModel2.UnlikePost(accessToken, post.id)
                }
            },
            onShareClick = {post ->
                viewModel2.sharePost(accessToken, post.id)
                Toast.makeText(this, "Đang chia sẻ: ${post.content}", Toast.LENGTH_SHORT).show()
            }
        )
        revRecentSearch.adapter = adapterNewsletter

        viewModel.resultSearch.observe(this) { listPosts ->
            if (listPosts != null) {
                if (page == 1) {
                    adapterNewsletter.setData(listPosts)
                    adapterNewsletter.setData(listPosts)
                } else {
                    adapterNewsletter.updateData(listPosts)
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                revRecentSearch.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                revRecentSearch.visibility = View.VISIBLE
            }
        }
        val nestedScrollView = findViewById<NestedScrollView>(R.id.myNested)
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _, _, _, _ ->
            if (!v.canScrollVertically(1)) {
                if (!viewModel.isLoading.value!! && !viewModel.isLastPage && accessToken.isNotEmpty()) {
                    Toast.makeText(this, "Đang tải thêm...", Toast.LENGTH_SHORT).show()
                    page += 1
                    viewModel.searchContent(accessToken, page, viewModel.currentQuery)
                }
            }
        }

        findViewById<EditText>(R.id.edtSearch).setOnEditorActionListener { v, actionId, event ->
            // v (view):Đây chính là cái EditText mà người dùng đang nhập liệu.Dùng để làm gì: Để bạn lấy dữ liệu trực tiếp từ ô nhập đó mà không cần gọi findViewById lại.
            // actionId: mã định danh của cái nút mà người dùng vừa bấm trên bàn phím ảo.
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    if (accessToken != null) {
                        page = 1;
                        viewModel.isLastPage = false;
                        viewModel.clearSearchData()
                        adapterNewsletter.setData(emptyList())
                        viewModel.searchContent(accessToken, page, query)
                        nestedScrollView.scrollTo(0, 0)
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
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
            viewModel2.reportPost(accessToken, postId, id, "Spam")
            Toast.makeText(this, "Báo cáo thành công spam!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        txtThongTinSai.setOnClickListener {
            viewModel2.reportPost(accessToken, postId, id, "Thông tin sai sự thật")
            Toast.makeText(this, "Báo cáo thành công thông tin sai!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        txtVandenhaycam.setOnClickListener {
            viewModel2.reportPost(accessToken, postId, id, "Nội dung nhạy cảm")
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

        viewModel2.comments.observe(this) { listComments ->
            if (!listComments.isNullOrEmpty()) {
                commentAdapter.updateData(listComments)
                rcvComments.scrollToPosition(listComments.size - 1)
            } else {
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