package com.example.ui_doan3tuan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostModel
import java.time.Instant
import java.time.Duration
import java.time.ZoneId
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

class AdapterNewsletter(private var list: List<PostModel>,val onCommentClick:(PostModel)-> Unit,val onReportClick:(PostModel)-> Unit,val onImageClick:(String)-> Unit,val onLikeClick:(PostModel)-> Unit): RecyclerView.Adapter<AdapterNewsletter.BangTinViewHolder>() {
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
        val ngayDang = Instant.parse(list[position].createdAt)
        val ngayHienTai = Instant.now()
        var khoangCach = Duration.between(ngayDang, ngayHienTai)
        val hienThiThoiGian = when{
            khoangCach.toMinutes()<1 ->"vừa xong"
            khoangCach.toMinutes()<60 ->"${khoangCach.toMinutes()} phút trước"
            khoangCach.toHours()<24 ->"${khoangCach.toHours()} giờ trước"
            else ->"${khoangCach.toDays()} ngày trước"
        }

        holder.txtTen.text = list[position].User.full_name?:"NGuyên Dương"
        holder.txtNoiDung.text = list[position].content
        holder.txtThoiGian.text =hienThiThoiGian
        holder.txtSoLuongThich.text = list[position].likeCount.toString()
        holder.txtSoLuongChiaSe.text = list[position].shareCount.toString()
        holder.txtSoLuongBinhLuan.text = list[position].commentCount.toString()

        var isLiked:Boolean  = false;
        holder.imgThich.setOnClickListener {
            isLiked=!isLiked;
            if(isLiked==true){
                list[position].likeCount = list[position].likeCount+1
                holder.txtSoLuongThich.text = list[position].likeCount.toString()
                holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
                holder.imgThich.setColorFilter(Color.parseColor("#FF0000"))
                onLikeClick(list[position])

            }else{
                list[position].likeCount = list[position].likeCount-1
                holder.txtSoLuongThich.text = list[position].likeCount.toString()
                holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
                holder.imgThich.setColorFilter(Color.parseColor("#FFFFFFFF"))
            }
        }

        val listMedia = list[position].PostMedia
        if(listMedia.isNotEmpty()){
            holder.revHienBaiDang.visibility = View.VISIBLE
            val imageAdapter = AdapterImage(listMedia)
            holder.revHienBaiDang.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.revHienBaiDang.adapter = imageAdapter

        }else{
            holder.revHienBaiDang.visibility = View.GONE

        }
        holder.imgBinhLuan.setOnClickListener {
            onCommentClick(list[position])
        }
        holder.imgReport.setOnClickListener {
            onReportClick(list[position])
        }
        if(list[position].User.avatar.toString().isNotEmpty()){
            val fullUrl ="http://10.0.2.2:8989/api/images/${list[position].User.avatar}"
            holder.imgDaiDien.load(fullUrl){
                crossfade(true)
            }
        }

        holder.imgDaiDien.setOnClickListener {
            onImageClick(list[position].User.id.toString())
        }

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
        var revHienBaiDang = itemView.findViewById<RecyclerView>(R.id.revHienBaiDang)
        var imgDaiDien = itemView.findViewById<ImageView>(R.id.imgAnhDaiDien_Home)
        var imgReport = itemView.findViewById<ImageView>(R.id.imgReport)







    }
    fun updateData(newList: List<PostModel>) {
        this.list = newList
        notifyDataSetChanged()
    }
}