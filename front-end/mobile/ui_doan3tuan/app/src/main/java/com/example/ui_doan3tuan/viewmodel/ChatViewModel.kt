package com.example.ui_doan3tuan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ui_doan3tuan.model.MessageModel
import com.example.ui_doan3tuan.service.socket.SocketService
import org.json.JSONObject
import kotlin.collections.mutableListOf

class ChatViewModel(
    private val repo: ChatRepository
): ViewModel() {
    val messages = MutableLiveData<MutableList<MessageModel>>()

    fun connectSocket(token:String, myUserId:Int){
        messages.value = mutableListOf()
        repo.connect(token){ json->
            val message = MessageModel(
                id = json.getInt("id"),
                conversation_id = json.getInt("conversation_id"),
                sender_id = json.getInt("sender_id"),
                content = json.getString("content"),
            )

            val list = messages.value?:mutableListOf()
            list.add(message)
            messages.postValue(list)
        }
    }

    fun sendMessage(conversation_id:Int, sender_id:Int, content:String) {
        repo.sendMessage(conversation_id, sender_id, content)
    }
}

class ChatRepository(
    private val socketService: SocketService
){
    fun connect(token:String, onMessage:(JSONObject)-> Unit){
        socketService.connect(token)
        socketService.onMessage(onMessage)
    }

    fun sendMessage(conversation_id:Int, sender_id:Int, content:String){
        socketService.sendMessage(conversation_id, sender_id, content )
    }
}

class ChatViewModelFactory(
    private val repo: ChatRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
