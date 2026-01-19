package com.example.ui_doan3tuan.service.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.Callback
import org.json.JSONObject

class SocketService {
    private lateinit var socket: Socket

    fun connect(token:String){
        Log.e("Token", token)
        val opts = IO.Options().apply {
            query = "token=$token"
            transports = arrayOf("websocket")
        }

        socket = IO.socket("http://10.0.2.2:8989", opts)
        socket.on(Socket.EVENT_CONNECT) {
            Log.d("SOCKET", "onnected: ${socket.id()}")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e("SOCKET", "Connect error: ${it[0]}")
        }
        socket.connect()
    }

    fun sendMessage(receiverId: Int, content: String) {
        val json = JSONObject()
        json.put("receiverId", receiverId)
        json.put("content", content)
        socket.emit("send_message_to_user", json)
    }



    fun onMessage(callback: (JSONObject) -> Unit) {
        socket.on("receive_message") {
            callback(it[0] as JSONObject)
        }

        socket.on("message_sent") {
            val data = it[0] as JSONObject
            callback(data.getJSONObject("message"))
        }
    }


}