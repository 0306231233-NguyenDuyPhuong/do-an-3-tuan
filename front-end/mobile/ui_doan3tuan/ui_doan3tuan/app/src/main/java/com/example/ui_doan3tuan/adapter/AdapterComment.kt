package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.UserModel

class AdapterComment(private var list: List<CommentModel>) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCommentUserName)
        val txtContent: TextView = view.findViewById(R.id.txtCommentContent)
        // val imgAvatar: ImageView = view.findViewById(R.id.imgCommentAvatar) // Nếu có ảnh
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
       return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.txtName.text = list[position].User.full_name ?: "Người dùng ẩn danh"
        holder.txtContent.text = list[position].content
    }
    override fun getItemCount(): Int = list.size


    fun updateData(newList: List<CommentModel>) {
        list = newList
        notifyDataSetChanged()
    }
}