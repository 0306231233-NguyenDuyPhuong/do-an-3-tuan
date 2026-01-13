package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R

class FriendsProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        // Lấy dữ liệu từ intent
        val friendId = intent.getIntExtra("friend_id", -1)
        val friendName = intent.getStringExtra("friend_name") ?: "Không có tên"
        val friendAvatar = intent.getStringExtra("friend_avatar")

        // Hiển thị thông tin
        val txtName = findViewById<TextView>(R.id.textView9)
        txtName.text = friendName

        // Load ảnh
        val imgAvatar = findViewById<ImageView>(R.id.imageView9)
        if (!friendAvatar.isNullOrEmpty()) {
            Glide.with(this)
                .load(friendAvatar)
                .placeholder(R.drawable.profile)
                .into(imgAvatar)
        }

        // Xử lý nút nhắn tin
        findViewById<Button>(R.id.btnNhanTin).setOnClickListener {
            //val intent = Intent(this, ChatActivity::class.java)
            //intent.putExtra("friend_id", friendId)
            //intent.putExtra("friend_name", friendName)
            //startActivity(intent)
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnKetBan).setOnClickListener {

        }
        findViewById<ImageView>(R.id.imgThoatFP).setOnClickListener {
            finish()
        }
    }
}