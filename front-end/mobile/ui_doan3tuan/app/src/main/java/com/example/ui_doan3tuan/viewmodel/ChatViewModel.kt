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
) : ViewModel() {

    val messages = MutableLiveData<MutableList<MessageModel>>(mutableListOf())

    fun connectSocket(token: String) {
        repo.connect(token) { json ->
            val message = MessageModel(
                id = json.getInt("id"),
                conversation_id = json.getInt("conversationId"),
                sender_id = json.getInt("senderId"),
                content = json.getString("content")
            )

            val list = messages.value ?: mutableListOf()
            list.add(message)
            messages.postValue(list)
        }
    }

    fun sendMessage(receiverId: Int, content: String) {
        repo.sendMessage(receiverId, content)
    }
}

class ChatRepository(
    private val socketService: SocketService
) {
    fun connect(token: String, onMessage: (JSONObject) -> Unit) {
        socketService.connect(token)
        socketService.onMessage(onMessage)
    }

    fun sendMessage(receiverId: Int, content: String) {
        socketService.sendMessage(receiverId, content)
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
