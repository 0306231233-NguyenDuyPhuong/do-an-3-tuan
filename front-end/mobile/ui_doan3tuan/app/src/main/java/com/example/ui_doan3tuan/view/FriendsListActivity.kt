package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class FriendsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_friends_list)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        findViewById<ImageView>(R.id.imgThoatLF).setOnClickListener {
            finish()
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_friend
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // CASE 1: Đang ở Home -> Bấm Home -> KHÔNG LÀM GÌ CẢ
                R.id.nav_home -> {
                    val intent = Intent(this, NewsletterActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.nav_friend -> {

                    return@setOnItemSelectedListener true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.nav_notification -> {
//                    // Bạn nhớ tạo Activity Thông Báo nhé, ví dụ: ThongBaoActivity
//                    val intent = Intent(this, ThongBaoActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                    startActivity(intent)
//                    overridePendingTransition(0, 0)
//                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    overridePendingTransition(0, 0)

                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}