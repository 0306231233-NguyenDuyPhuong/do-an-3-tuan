package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostModel
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter(
    private val posts: MutableList<PostModel>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgAvatar: ShapeableImageView =
            view.findViewById(R.id.imgAnhDaiDien_Home)
        val txtName: TextView =
            view.findViewById(R.id.txtName_BaiDang)
        val txtHour: TextView =
            view.findViewById(R.id.txtHour_BaiDang)

        val txtContent: TextView =
            view.findViewById(R.id.txtConten_BaiDang)
        val txtXemThem: TextView =
            view.findViewById(R.id.txtXemThem)

        val revMedia: RecyclerView =
            view.findViewById(R.id.revHienBaiDang)
        val txtIndicator: TextView =
            view.findViewById(R.id.txtIndicator)

        val imgLike: ImageView =
            view.findViewById(R.id.imgTim_BaiDang)
        val txtLikeCount: TextView =
            view.findViewById(R.id.txtSoLuongTim_BaiDang)

        val imgComment: ImageView =
            view.findViewById(R.id.imgBinhLuan_BaiDang)
        val txtCommentCount: TextView =
            view.findViewById(R.id.txtSLBinhLuan_BaiDang)

        val imgShare: ImageView =
            view.findViewById(R.id.imgChiaSe)
        val txtShareCount: TextView =
            view.findViewById(R.id.txtSoLuongChiaSe_BaiDang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_newsletter, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]


        holder.txtName.text = post.User.full_name
        Glide.with(holder.itemView.context)
            .load(post.User.avatar)
            .placeholder(R.drawable.profile)
            .into(holder.imgAvatar)

        holder.txtHour.text = post.createdAt

        holder.txtContent.text = post.content
        holder.txtContent.maxLines = if (post.isExpanded) Int.MAX_VALUE else 3

        if (post.content.length > 120) {
            holder.txtXemThem.visibility = View.VISIBLE
            holder.txtXemThem.text =
                if (post.isExpanded) "Thu gọn" else "Xem thêm"
        } else {
            holder.txtXemThem.visibility = View.GONE
        }

        holder.txtXemThem.setOnClickListener {
            post.isExpanded = !post.isExpanded
            notifyItemChanged(position)
        }

        holder.txtLikeCount.text = post.likeCount.toString()
        holder.txtCommentCount.text = post.commentCount.toString()
        holder.txtShareCount.text = post.shareCount.toString()

        holder.imgLike.setImageResource(
            if (post.is_liked)
                R.drawable.baseline_favorite_24
            else
                R.drawable.btn_3
        )


        bindMedia(holder, post)
    }

    private fun bindMedia(holder: PostViewHolder, post: PostModel) {
        if (post.PostMedia.isEmpty()) {
            holder.revMedia.visibility = View.GONE
            holder.txtIndicator.visibility = View.GONE
            return
        }

        holder.revMedia.visibility = View.VISIBLE
        holder.revMedia.layoutManager =
            LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
        holder.revMedia.adapter = PostMediaAdapter(post.PostMedia)

        if (post.PostMedia.size > 1) {
            holder.txtIndicator.visibility = View.VISIBLE
            holder.txtIndicator.text = "1/${post.PostMedia.size}"
        } else {
            holder.txtIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = posts.size

    fun setData(newData: List<PostModel>) {
        posts.clear()
        posts.addAll(newData)
        notifyDataSetChanged()
    }
}
