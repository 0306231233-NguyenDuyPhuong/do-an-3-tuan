package com.example.ui_doan3tuan.viewmodel

import com.example.ui_doan3tuan.model.MessageModel
import com.example.ui_doan3tuan.model.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.encodeToString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request

class MessageViewModel(
    private val client: OkHttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun getMessage(token: String, id:Int): MutableList<MessageModel> = withContext(Dispatchers.IO){
        val messageList = mutableListOf<MessageModel>()
        val url = "http://10.0.2.2:8989/api/messages/${id}"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${token}")
            .build()
        client.newCall(request).execute().use { response ->
            val bodyString = response.body?.string()?:""
            if(!response.isSuccessful){
                throw Exception(response.message)
            }
            val data = json.decodeFromString<MessageResponse>(bodyString)
            messageList.addAll(data.data)
            return@use messageList
        }
    }

    suspend fun postMessage(token:String, message: MessageModel): Int =
        withContext(Dispatchers.IO) {

            val url = "http://10.0.2.2:8989/api/messages"

            val requestBody = json.encodeToString(message)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${token}")
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    throw Exception(response.message)
                }

                return@use response.code
            }
        }
}
