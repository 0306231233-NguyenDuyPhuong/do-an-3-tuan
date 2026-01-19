package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.ApdaterConversation
import com.example.ui_doan3tuan.model.ConversationMemberModel
import com.example.ui_doan3tuan.model.ConversationModel
import com.example.ui_doan3tuan.viewmodel.ConversationViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class ConversationActivity : AppCompatActivity(), ApdaterConversation.OnClickItemConversation {
    private val client = OkHttpClient()
    private val conversationViewModel = ConversationViewModel(client)
    private lateinit var rcvConversation: RecyclerView
    private var conversationList = mutableListOf<ConversationModel>()
    private lateinit var sharedPref: SharedPreferences
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conversation)
        sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        token = sharedPref.getString("access_token", "") ?: ""

        rcvConversation = findViewById(R.id.rcvConversation)

        lifecycleScope.launch {
            conversationList.addAll(conversationViewModel.getConversation(token))
            rcvConversation.layoutManager = LinearLayoutManager(this@ConversationActivity)
            rcvConversation.adapter = ApdaterConversation(conversationList, this@ConversationActivity)
        }
    }

    override fun onClickItem(postion: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("conversation_id", conversationList[postion].id)
        intent.putExtra("id", conversationList[postion].members[1].User.id)
        intent.putExtra("full_name", conversationList[postion].members[1].User.full_name)
        intent.putExtra("avatar", conversationList[postion].members[1].User.avatar)
        startActivity(intent)
    }
}