package com.example.ui_doan3tuan.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterNewsletter
import com.example.ui_doan3tuan.model.PostModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
class NewsletterActivity : AppCompatActivity() {

    // Khai báo ViewModel
    private val viewModel: NewsletterViewModel by viewModels()

    // Khai báo Adapter (nên dùng lateinit hoặc khởi tạo sớm)
    private lateinit var adapterNewsletter: AdapterNewsletter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newsletter)

        val revHienBaiDang = findViewById<RecyclerView>(R.id.revHienBaiDang)

        // 1. Thiết lập giao diện cơ bản
        revHienBaiDang.layoutManager = LinearLayoutManager(this)

        // Khởi tạo adapter với danh sách trống ban đầu
        adapterNewsletter = AdapterNewsletter(mutableListOf())
        revHienBaiDang.adapter = adapterNewsletter

        // 2. Xử lý Edge-to-Edge (Để tránh nội dung bị lấp dưới thanh hệ thống)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Lắng nghe dữ liệu từ ViewModel (Quan trọng nhất của MVVM)
        viewModel.posts.observe(this) { listPosts ->
            if (listPosts != null) {
                // Nếu Adapter của bạn có hàm cập nhật dữ liệu (ví dụ setData) thì gọi nó
                // Hoặc đơn giản là tạo mới nếu bạn chưa làm hàm update
                val newAdapter = AdapterNewsletter(listPosts)
                revHienBaiDang.adapter = newAdapter
            }
        }

        // 4. Lắng nghe lỗi (Đừng quên bước này nhé)
        viewModel.error.observe(this) { message ->
            if (message != null) {
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Gọi lấy dữ liệu
        viewModel.getProduct()
    }
}