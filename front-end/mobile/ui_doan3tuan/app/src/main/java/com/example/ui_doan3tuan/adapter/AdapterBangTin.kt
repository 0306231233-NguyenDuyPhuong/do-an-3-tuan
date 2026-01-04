package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostModel

class AdapterBangTin(private var list: List<PostModel>): RecyclerView.Adapter<AdapterBangTin.BangTinViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BangTinViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return BangTinViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BangTinViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class BangTinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTen = itemView.findViewById<TextView>(R.id.txtName_Home)
        var txtNoiDung = itemView.findViewById<TextView>(R.id.txtConten_Home)
        var txtThoiGian = itemView.findViewById<TextView>(R.id.txtHour_Home)
        var txtSoLuongThich = itemView.findViewById<TextView>(R.id.txtSoLuongTim_Home)
        var txtSoLuongBinhLuan = itemView.findViewById<TextView>(R.id.txtSLBinhLuan_Home)
        var txtSoLuongChiaSe = itemView.findViewById<TextView>(R.id.txtSoLuongChiaSe_Home)
        var imgThich = itemView.findViewById<ImageView>(R.id.imgTim_Home)
        var imgBinhLuan = itemView.findViewById<ImageView>(R.id.imgBinhLuan_Home)
        var imgChiaSe = itemView.findViewById<ImageView>(R.id.imgChiaSe_Home)
        var imgHienBaiDang = itemView.findViewById<ImageView>(R.id.imgItem_Home)
        var imgDaiDien = itemView.findViewById<ImageView>(R.id.imgAnhDaiDien_Home)







    }
}