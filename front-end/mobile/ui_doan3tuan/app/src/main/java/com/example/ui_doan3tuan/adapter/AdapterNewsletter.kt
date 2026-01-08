package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostModel

class AdapterNewsletter(private var list: List<PostModel>): RecyclerView.Adapter<AdapterNewsletter.BangTinViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BangTinViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_newsletter, parent, false)
        return BangTinViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BangTinViewHolder,
        position: Int
    ) {
        holder.txtTen.text = list[position].User.full_name ?:"NGuyên Dương"
        holder.txtNoiDung.text = list[position].content
        holder.txtThoiGian.text = list[position].created_at
        holder.txtSoLuongThich.text = list[position].likeCount.toString()
        holder.txtSoLuongChiaSe.text = list[position].shareCount.toString()
        holder.txtSoLuongBinhLuan.text = list[position].commentCount.toString()



    }

    override fun getItemCount(): Int {
        return list.size
    }

    class BangTinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTen = itemView.findViewById<TextView>(R.id.txtName_BaiDang)
        var txtNoiDung = itemView.findViewById<TextView>(R.id.txtConten_BaiDang)
        var txtThoiGian = itemView.findViewById<TextView>(R.id.txtHour_BaiDang)
        var txtSoLuongThich = itemView.findViewById<TextView>(R.id.txtSoLuongTim_BaiDang)
        var txtSoLuongBinhLuan = itemView.findViewById<TextView>(R.id.txtSLBinhLuan_BaiDang)
        var txtSoLuongChiaSe = itemView.findViewById<TextView>(R.id.txtSoLuongChiaSe_BaiDang)
        var imgThich = itemView.findViewById<ImageView>(R.id.imgTim_BaiDang)
        var imgBinhLuan = itemView.findViewById<ImageView>(R.id.imgBinhLuan_BaiDang)
        var imgChiaSe = itemView.findViewById<ImageView>(R.id.imgChiaSe)
        var imgHienBaiDang = itemView.findViewById<ImageView>(R.id.imgBaiDang)
        var imgDaiDien = itemView.findViewById<ImageView>(R.id.imgAnhDaiDien_Home)







    }
}