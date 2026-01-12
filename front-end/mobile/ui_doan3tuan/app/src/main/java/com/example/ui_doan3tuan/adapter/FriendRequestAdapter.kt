package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.FriendRequest

class FriendRequestAdapter(
    private var requestList: List<FriendRequest>,
    private val onAcceptClick: (FriendRequest) -> Unit,
    private val onRejectClick: (FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val btnAccept: Button = itemView.findViewById(R.id.acceptButton)
        val btnReject: Button = itemView.findViewById(R.id.rejectButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requestList[position]

        // Hiển thị tên người gửi
        holder.txtName.text = request.sender.full_name

        // Load ảnh
        if (!request.sender.avatar.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(request.sender.avatar)
                .placeholder(R.drawable.profile)
                .into(holder.imgAvatar)
        } else {
            // Nếu không có ảnh, dùng ảnh mặc định
            holder.imgAvatar.setImageResource(R.drawable.profile)
        }

        // Xử lý nút chấp nhận
        holder.btnAccept.setOnClickListener {
            onAcceptClick(request)
        }

        // Xử lý nút từ chối
        holder.btnReject.setOnClickListener {
            onRejectClick(request)
        }
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    fun updateList(newList: List<FriendRequest>) {
        (requestList as MutableList).clear()
        (requestList as MutableList).addAll(newList)
        notifyDataSetChanged()
    }
}