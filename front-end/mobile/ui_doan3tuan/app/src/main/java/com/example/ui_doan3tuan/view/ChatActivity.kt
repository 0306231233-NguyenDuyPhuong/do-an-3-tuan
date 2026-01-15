package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R

class ChatActivity : AppCompatActivity() {
    private lateinit var imgExitChat: ImageView
    private lateinit var txtFullNameChat: TextView
    private lateinit var imgAvatarChat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val avatar = intent.getStringExtra("avatar")
        txtFullNameChat = findViewById(R.id.txtFullNameChat)
        imgAvatarChat = findViewById(R.id.imgAvatarChat)
        Glide.with(this)
            .load("http://10.0.2.2:8989/api/images/${avatar}")
            .into(imgAvatarChat)

        txtFullNameChat.setText(intent.getStringExtra("full_name"))
        imgExitChat = findViewById(R.id.imgExitChat)

        imgExitChat.setOnClickListener {finish()}
    }
}