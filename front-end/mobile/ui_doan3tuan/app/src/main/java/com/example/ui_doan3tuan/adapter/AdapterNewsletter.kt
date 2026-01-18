package com.example.ui_doan3tuan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.PostModel
import java.time.Duration
import java.time.Instant

class AdapterNewsletter(
    private var list: MutableList<PostModel>,
    val onCommentClick: (PostModel) -> Unit,
    val onReportClick: (PostModel) -> Unit,
    val onImageClick: (String) -> Unit,
    val onLikeClick: (PostModel, Boolean) -> Unit,
    val onShareClick: (PostModel) -> Unit
) : RecyclerView.Adapter<AdapterNewsletter.BangTinViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BangTinViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_newsletter, parent, false)
        return BangTinViewHolder(view)
    }


    override fun onBindViewHolder(holder: BangTinViewHolder, position: Int) {
        val ngayDang = Instant.parse(list[position].createdAt)
        val ngayHienTai = Instant.now()
        val khoangCach = Duration.between(ngayDang, ngayHienTai)
        val hienThiThoiGian = when {
            khoangCach.toMinutes() < 1 -> "vừa xong"
            khoangCach.toMinutes() < 60 -> "${khoangCach.toMinutes()} phút trước"
            khoangCach.toHours() < 24 -> "${khoangCach.toHours()} giờ trước"
            else -> "${khoangCach.toDays()} ngày trước"
        }

        holder.txtTen.text = list[position].User.full_name ?: "Nguyên Dương"
        holder.txtThoiGian.text = hienThiThoiGian
        holder.txtSoLuongThich.text = list[position].likeCount.toString()
        holder.txtSoLuongChiaSe.text = list[position].shareCount.toString()
        holder.txtSoLuongBinhLuan.text = list[position].commentCount.toString()

        val content = list[position].content
        val wordCount = content.trim().split("\\s+".toRegex()).size

        if (wordCount > 100) {
            holder.txtXemThem.visibility = View.VISIBLE

            if (list[position].isExpanded) {
                holder.txtNoiDung.text = content
                holder.txtXemThem.text = "Thu gọn"
            } else {
                holder.txtNoiDung.text = getShortContent(content)
                holder.txtXemThem.text = "Xem thêm"
            }

            holder.txtXemThem.setOnClickListener {
                list[position].isExpanded = !list[position].isExpanded
                notifyItemChanged(position)
            }
        } else {
            holder.txtNoiDung.text = content
            holder.txtXemThem.visibility = View.GONE
        }
        holder.txtXemThem.setOnClickListener {
            list[position].isExpanded = !list[position].isExpanded
            notifyItemChanged(position)
        }

        if (list[position].is_liked) {
            holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
            holder.imgThich.setColorFilter(Color.parseColor("#FF0000"))
        } else {
            holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
            holder.imgThich.setColorFilter(Color.parseColor("#FFFFFFFF"))
        }
        holder.imgThich.setOnClickListener {
            if (list[position].is_liked) {
                list[position].is_liked = false
                list[position].likeCount = list[position].likeCount - 1

                holder.imgThich.setColorFilter(Color.parseColor("#FFFFFFFF"))
                holder.txtSoLuongThich.text = list[position].likeCount.toString()
                onLikeClick(list[position], false)

            } else {
                list[position].is_liked = true
                list[position].likeCount = list[position].likeCount + 1

                holder.imgThich.setColorFilter(Color.parseColor("#FF0000"))
                holder.txtSoLuongThich.text = list[position].likeCount.toString()
                onLikeClick(list[position], true)
            }
        }

        val listMedia = list[position].PostMedia
        if (listMedia.isNotEmpty()) {
            holder.revHienBaiDang.visibility = View.VISIBLE
            val imageAdapter = AdapterImage(listMedia)
            holder.revHienBaiDang.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.revHienBaiDang.adapter = imageAdapter
        } else {
            holder.revHienBaiDang.visibility = View.GONE
        }

        holder.imgBinhLuan.setOnClickListener { onCommentClick(list[position]) }
        holder.imgReport.setOnClickListener { onReportClick(list[position]) }

        if (list[position].User.avatar.toString().isNotEmpty()) {
            val fullUrl = "http://10.0.2.2:8989/api/images/${list[position].User.avatar}"
            holder.imgDaiDien.load(fullUrl) {
                crossfade(true)
                error(R.drawable.profile)
                placeholder(R.drawable.profile)
            }
        } else {
            holder.imgDaiDien.load(R.drawable.profile)
        }

        holder.imgDaiDien.setOnClickListener {
            onImageClick(list[position].User.id.toString())
        }
        holder.imgChiaSe.setOnClickListener {
            onShareClick(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class BangTinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTen: TextView = itemView.findViewById(R.id.txtName_BaiDang)
        var txtNoiDung: TextView = itemView.findViewById(R.id.txtConten_BaiDang)
        var txtThoiGian: TextView = itemView.findViewById(R.id.txtHour_BaiDang)
        var txtSoLuongThich: TextView = itemView.findViewById(R.id.txtSoLuongTim_BaiDang)
        var txtSoLuongBinhLuan: TextView = itemView.findViewById(R.id.txtSLBinhLuan_BaiDang)
        var txtSoLuongChiaSe: TextView = itemView.findViewById(R.id.txtSoLuongChiaSe_BaiDang)
        var imgThich: ImageView = itemView.findViewById(R.id.imgTim_BaiDang)
        var imgBinhLuan: ImageView = itemView.findViewById(R.id.imgBinhLuan_BaiDang)
        var imgChiaSe: ImageView = itemView.findViewById(R.id.imgChiaSe)
        var revHienBaiDang: RecyclerView = itemView.findViewById(R.id.revHienBaiDang)
        var imgDaiDien: ImageView = itemView.findViewById(R.id.imgAnhDaiDien_Home)
        var imgReport: ImageView = itemView.findViewById(R.id.imgReport)
        val txtXemThem: TextView = itemView.findViewById(R.id.txtXemThem)
    }
    private fun getShortContent(text: String, maxWords: Int = 100): String {
        val words = text.trim().split("\\s+".toRegex())
        return words.take(maxWords).joinToString(" ") + "..."
    }
    fun setData(newList: List<PostModel>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateData(newList: List<PostModel>) {
        val oldSize = this.list.size
        this.list.addAll(newList)
        notifyItemRangeInserted(oldSize, newList.size)
    }
}