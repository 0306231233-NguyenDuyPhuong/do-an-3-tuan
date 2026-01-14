package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.ChatAdapter
import com.example.ui_doan3tuan.model.Message
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    private val currentUserId = "userA"
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var edtMessage: EditText
    private lateinit var btnSendMessage: FloatingActionButton
    private lateinit var imgThoatChat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        edtMessage = findViewById(R.id.edtMessage)
        imgThoatChat = findViewById(R.id.imgThoatChat)

        adapter = ChatAdapter(messages, currentUserId)
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        loadMessageData()
        imgThoatChat.setOnClickListener { finish() }
        btnSendMessage.setOnClickListener {
            val text = edtMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val newMessage = Message(senderId = currentUserId, content = text)
                messages.add(newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                chatRecyclerView.scrollToPosition(messages.size - 1)
                edtMessage.text.clear()

                // Tạo tin nhắn phản hồi giả lập
                val reply = Message(senderId = "userB", content = "Reply: $text")
                messages.add(reply)
                adapter.notifyItemInserted(messages.size - 1)
                chatRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun loadMessageData() {
        messages.add(Message("userB", "Hi there!"))
        messages.add(Message("userA", "Hello! How are you?"))
        messages.add(Message("userB", "I'm good, thanks! And you?"))
        messages.add(Message("userA", "I'm fine too."))

        adapter.notifyDataSetChanged()
    }
}