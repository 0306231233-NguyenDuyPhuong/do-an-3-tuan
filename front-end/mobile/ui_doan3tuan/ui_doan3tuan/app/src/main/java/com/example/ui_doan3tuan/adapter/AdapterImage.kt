package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostMediaModel

class AdapterImage(private val images: List<PostMediaModel>) :
    RecyclerView.Adapter<AdapterImage.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val media = images[position]
        val fullUrl = "http://10.0.2.2:8989/api/images/${media.media_url}"

        holder.img.load(fullUrl) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int = images.size
}