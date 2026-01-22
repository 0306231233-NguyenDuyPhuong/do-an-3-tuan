package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.model.ConversationMemberModel
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.ConversationModel

class ApdaterConversation(
    private val conversatitonList: MutableList<ConversationModel>,
    private val listener: OnClickItemConversation
): RecyclerView.Adapter<ApdaterConversation.ConverstationViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConverstationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation_layout, parent, false)
        return ConverstationViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ConverstationViewHolder,
        position: Int
    ) {
        val item = conversatitonList[position]
        holder.txtNameUserConversation.setText(item.members[0].User.full_name)
        holder.txtMessageUser.setText(item.messages[0].content)
        Glide.with(holder.itemView.context)
            .load("http://10.0.2.2:8989/api/images/${item.members[0].User.avatar}")
            .into(holder.imgUserConverstation)
        holder.itemView.setOnClickListener {
            listener.onClickItem(position)
        }
    }

    override fun getItemCount(): Int = conversatitonList.size

    interface OnClickItemConversation{
        fun onClickItem(postion:Int)
    }
    class ConverstationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgUserConverstation = itemView.findViewById<ImageView>(R.id.imgUserConverstation)
        val txtNameUserConversation = itemView.findViewById<TextView>(R.id.txtNameUserConversation)
        val txtMessageUser = itemView.findViewById<TextView>(R.id.txtMessageUser)
    }
}