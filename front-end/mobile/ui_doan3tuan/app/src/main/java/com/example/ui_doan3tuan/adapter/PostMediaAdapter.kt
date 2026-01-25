package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostMediaModel

class PostMediaAdapter(
    private val mediaList: List<PostMediaModel>
) : RecyclerView.Adapter<PostMediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMedia: ImageView = view.findViewById(R.id.imgChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]

        val fullUrl = "http://10.0.2.2:8989/api/images/${media.media_url}"
        Glide.with(holder.itemView.context)
            .load(fullUrl)
            .into(holder.imgMedia)
    }

    override fun getItemCount(): Int = mediaList.size
}
