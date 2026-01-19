package com.example.ui_doan3tuan.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.model.MessageModel
import com.example.ui_doan3tuan.R

class AdapterChat(
    private val messagesList: MutableList<MessageModel>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val   TYPE_LEFT = 0
        private const val   TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(messagesList[position].sender_id == 1) TYPE_RIGHT else TYPE_LEFT
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if(viewType == TYPE_RIGHT){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_right, parent, false)
            RightViewHolder(view)
        } else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_left, parent, false)
            LeftViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val message = messagesList[position]
        if(holder is RightViewHolder){
            holder.txtMessage.setText(message.content)
        } else if(holder is LeftViewHolder){
            holder.txtMessage.setText(message.content)
        }
    }

    override fun getItemCount(): Int = messagesList.size


    class RightViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)

    }

    class LeftViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
    }
}