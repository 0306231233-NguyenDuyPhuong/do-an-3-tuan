package com.example.ui_doan3tuan.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
class AdapterSelectImageAndVideo(
    private val onItemClick: (Uri) -> Unit // Thêm Callback này
) : ListAdapter<Uri, AdapterSelectImageAndVideo.MediaVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_video, parent, false)
        return MediaVH(view, onItemClick)
    }

    override fun onBindViewHolder(holder: MediaVH, position: Int) {
        holder.bind(getItem(position))
    }

    class MediaVH(
        itemView: View,
        private val onItemClick: (Uri) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgPreview)
        private val videoIcon: ImageView = itemView.findViewById(R.id.imgVideoIcon)
        private val imgTick: ImageView = itemView.findViewById(R.id.imgDelete)

        fun bind(uri: Uri) {
            imageView.setImageURI(uri)

            val type = itemView.context.contentResolver.getType(uri)
            videoIcon.visibility = if (type?.startsWith("video") == true) View.VISIBLE else View.GONE
            imgTick.visibility = View.VISIBLE // Luôn hiện tick vì đã chọn

            // Khi nhấn vào ảnh thì thực hiện hàm onItemClick để xóa
            itemView.setOnClickListener {
                onItemClick(uri)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(old: Uri, new: Uri) = old == new
        override fun areContentsTheSame(old: Uri, new: Uri) = old == new
    }
}