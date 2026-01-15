package com.example.ui_doan3tuan.viewmodel

import com.example.ui_doan3tuan.model.ConversationMemberModel
import com.example.ui_doan3tuan.model.ConversationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ConversationViewModel(
    private val client: OkHttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun getConversation(token:String): MutableList<ConversationMemberModel> = withContext(Dispatchers.IO){
        val conversatitonList = mutableListOf<ConversationMemberModel>()
        val url = "http://10.0.2.2:8989/api/conversations"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${token}")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            val bodyString = response.body?.string()?:""
            if(!response.isSuccessful){
                throw Exception(response.message)
            }
            val conversation = json.decodeFromString<ConversationResponse>(bodyString)
            conversatitonList.addAll(conversation.data[0].members)
            return@use conversatitonList
        }
    }
}