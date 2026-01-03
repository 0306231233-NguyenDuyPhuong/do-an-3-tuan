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

class AdapterChonAnhVideo: ListAdapter<Uri, AdapterChonAnhVideo.MediaVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anh_video, parent, false)
        return MediaVH(view)
    }

    override fun onBindViewHolder(holder: MediaVH, position: Int) {
        holder.bind(getItem(position))
    }

    class MediaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgPreview)
        private val videoIcon: ImageView = itemView.findViewById(R.id.imgVideoIcon)

        fun bind(uri: Uri) {
            val type = itemView.context.contentResolver.getType(uri)
            val isVideo = type?.startsWith("video") == true

            imageView.setImageURI(uri)
            videoIcon.visibility = if (isVideo) View.VISIBLE else View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(old: Uri, new: Uri) = old == new
        override fun areContentsTheSame(old: Uri, new: Uri) = old == new
    }
}