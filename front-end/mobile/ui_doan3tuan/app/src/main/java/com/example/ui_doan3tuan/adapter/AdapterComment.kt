package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.CommentModel
import com.example.ui_doan3tuan.model.UserModel
import java.time.Duration
import java.time.Instant

class AdapterComment(private var list: List<CommentModel>) :
    RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCommentUserName)
        val txtContent: TextView = view.findViewById(R.id.txtCommentContent)
        val imgAvatar: ImageView = view.findViewById(R.id.imgCommentAvatar)
        val txtCommentTime: TextView = view.findViewById(R.id.txtCommentTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        val ngayDang = Instant.parse(list[position].created_at)
        val ngayHienTai = Instant.now()
        val khoangCach = Duration.between(ngayDang, ngayHienTai)
        val hienThiThoiGian = when {
            khoangCach.toMinutes() < 1 -> "vừa xong"
            khoangCach.toMinutes() < 60 -> "${khoangCach.toMinutes()} phút trước"
            khoangCach.toHours() < 24 -> "${khoangCach.toHours()} giờ trước"
            else -> "${khoangCach.toDays()} ngày trước"
        }

        holder.txtName.text = list[position].User.full_name ?: "Người dùng ẩn danh"
        holder.txtContent.text = list[position].content
        holder.txtCommentTime.text = hienThiThoiGian
        if (list[position].User.avatar.toString().isNotEmpty()) {
            val fullUrl = "http://10.0.2.2:8989/api/images/${list[position].User.avatar}"
            holder.imgAvatar.load(fullUrl) {
                crossfade(true)
                error(R.drawable.profile)
                placeholder(R.drawable.profile)
            }
        } else {
            holder.imgAvatar.load(R.drawable.profile)
        }


    }

    override fun getItemCount(): Int = list.size


    fun updateData(newList: List<CommentModel>) {
        list = newList
        notifyDataSetChanged()
    }
}