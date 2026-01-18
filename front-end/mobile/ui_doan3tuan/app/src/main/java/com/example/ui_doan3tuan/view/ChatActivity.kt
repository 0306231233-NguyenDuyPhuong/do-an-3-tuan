package com.example.ui_doan3tuan.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.service.socket.SocketService
import com.example.ui_doan3tuan.viewmodel.ChatRepository
import com.example.ui_doan3tuan.viewmodel.ChatViewModel
import com.example.ui_doan3tuan.viewmodel.ChatViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {
    private lateinit var imgExitChat: ImageView
    private lateinit var txtFullNameChat: TextView
    private lateinit var imgAvatarChat: ImageView
    private lateinit var edtMessage: TextView
    private lateinit var btnSendMesseage: FloatingActionButton
    private lateinit var viewModel: ChatViewModel
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val socketService = SocketService()
        val repository = ChatRepository(socketService)
        val factory = ChatViewModelFactory(repository)
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = 1
        token = sharedPref.getString("access_token", "") ?: ""

        viewModel = ViewModelProvider(this, factory)
            .get(ChatViewModel::class.java)
        viewModel.connectSocket(token, userId)


        edtMessage = findViewById(R.id.edtMessage)
        btnSendMesseage = findViewById(R.id.btnSendMessage)

        val avatar = intent.getStringExtra("avatar")
        txtFullNameChat = findViewById(R.id.txtFullNameChat)
        imgAvatarChat = findViewById(R.id.imgAvatarChat)
        Glide.with(this)
            .load("http://10.0.2.2:8989/api/images/${avatar}")
            .into(imgAvatarChat)

        txtFullNameChat.setText(intent.getStringExtra("full_name"))
        imgExitChat = findViewById(R.id.imgExitChat)

        imgExitChat.setOnClickListener {finish()}

        btnSendMesseage.setOnClickListener {
            viewModel.sendMessage(1, 1, "alo")
        }
    }
}