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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterChat
import com.example.ui_doan3tuan.model.MessageModel
import com.example.ui_doan3tuan.service.socket.SocketService
import com.example.ui_doan3tuan.viewmodel.ChatRepository
import com.example.ui_doan3tuan.viewmodel.ChatViewModel
import com.example.ui_doan3tuan.viewmodel.ChatViewModelFactory
import com.example.ui_doan3tuan.viewmodel.MessageViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.math.log

class ChatActivity : AppCompatActivity() {
    private lateinit var imgExitChat: ImageView
    private lateinit var txtFullNameChat: TextView
    private lateinit var imgAvatarChat: ImageView
    private lateinit var edtMessage: TextView
    private lateinit var btnSendMesseage: FloatingActionButton
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: AdapterChat
    private val client = OkHttpClient()
    private var messageViewModel = MessageViewModel(client)
    private var messageList = mutableListOf<MessageModel>()
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        edtMessage = findViewById(R.id.edtMessage)
        btnSendMesseage = findViewById(R.id.btnSendMessage)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        val avatar = intent.getStringExtra("avatar")
        txtFullNameChat = findViewById(R.id.txtFullNameChat)
        imgAvatarChat = findViewById(R.id.imgAvatarChat)

        val socketService = SocketService()
        val repository = ChatRepository(socketService)
        val factory = ChatViewModelFactory(repository)
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = 1
        val receiverId = intent.getIntExtra("id", 0)
        val conversationId = intent.getIntExtra("conversation_id", 0)
        Log.d("conversation_id", conversationId.toString())

        token = sharedPref.getString("access_token", "") ?: ""

        viewModel = ViewModelProvider(this, factory)
            .get(ChatViewModel::class.java)
        viewModel.connectSocket(token)
        chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        adapter = AdapterChat(messageList)
        chatRecyclerView.adapter = adapter

        Glide.with(this)
            .load("http://10.0.2.2:8989/api/images/${avatar}")
            .into(imgAvatarChat)

        txtFullNameChat.setText(intent.getStringExtra("full_name"))
        imgExitChat = findViewById(R.id.imgExitChat)

        imgExitChat.setOnClickListener {finish()}

        viewModel.messages.observe(this) { list ->
            messageList.clear()
            messageList.addAll(list)
            adapter.notifyDataSetChanged()

            if (messageList.isNotEmpty()) {
                chatRecyclerView.scrollToPosition(messageList.size - 1)
            }
        }

        lifecycleScope.launch {
            messageList.clear()
            messageList.addAll(messageViewModel.getMessage(token, receiverId))
            adapter.notifyDataSetChanged()
        }

        btnSendMesseage.setOnClickListener {
            val content = edtMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.sendMessage(receiverId, content)
                edtMessage.setText("")
            }
        }

    }
}