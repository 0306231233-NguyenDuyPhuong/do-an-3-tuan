package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.model.Message
import com.example.ui_doan3tuan.R

class ChatAdapter(
    private val messages: List<Message>,
    private val currentUserId: String
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val  VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int =
        if(messages[position].senderId == currentUserId) VIEW_TYPE_RIGHT else VIEW_TYPE_LEFT

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewType == VIEW_TYPE_RIGHT){
            RigthMessageViewHolder(inflater.inflate(R.layout.item_message_right, parent, false))
        } else{
            LeftMessageViewHolder(inflater.inflate(R.layout.item_message_left, parent, false))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val message = messages[position]
        if(holder is RigthMessageViewHolder){
            holder.txtMessage.setText(message.content)
        } else if(holder is LeftMessageViewHolder){
            holder.txtMessage.setText(message.content)
        }
    }

    override fun getItemCount(): Int = messages.size

    class RigthMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
    }
    class LeftMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
    }
}