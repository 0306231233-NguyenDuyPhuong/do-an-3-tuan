package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.Friend

class FriendsAdapter(
    private var friendsList: List<Friend>,
    private val onItemClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgBanBe)
        val txtName: TextView = itemView.findViewById(R.id.txtTenBaBe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.txtName.text = friend.full_name
        if (!friend.avatar.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load("http://10.0.2.2:8989/api/images/${friend.avatar}")
                .placeholder(R.drawable.profile)
                .into(holder.imgAvatar)
        }

        // Xử lý click
        holder.itemView.setOnClickListener {
            onItemClick(friend)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    fun updateList(newList: List<Friend>) {
        friendsList = newList
        notifyDataSetChanged()
    }
}