package com.example.ui_doan3tuan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterNewsletter.BangTinViewHolder
import com.example.ui_doan3tuan.model.PostModel
import java.time.Duration
import java.time.Instant

class AdapterUserProfile(
    private var list: MutableList<PostModel>,
    val onCommentClick:(PostModel)-> Unit,
    val onReportClick:(PostModel)-> Unit,
    val onLikeClick: (PostModel, Boolean) -> Unit,
    val onShareClick: (PostModel) -> Unit,
    val onImageClick: (String) -> Unit
): RecyclerView.Adapter<AdapterUserProfile.UserProfileViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserProfileViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_newsletter, parent, false)
        return UserProfileViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UserProfileViewHolder,
        position: Int
    ) {
        val currentItem = list[position]

        val ngayDang = Instant.parse(currentItem.createdAt)
        val ngayHienTai = Instant.now()
        val khoangCach = Duration.between(ngayDang, ngayHienTai)
        val hienThiThoiGian = when {
            khoangCach.toMinutes() < 1 -> "vừa xong"
            khoangCach.toMinutes() < 60 -> "${khoangCach.toMinutes()} phút trước"
            khoangCach.toHours() < 24 -> "${khoangCach.toHours()} giờ trước"
            else -> "${khoangCach.toDays()} ngày trước"
        }

        holder.txtTen.text = currentItem.User.full_name ?: "Nguyên Dương"
        holder.txtThoiGian.text = hienThiThoiGian
        holder.txtSoLuongThich.text = currentItem.likeCount.toString()
        holder.txtSoLuongChiaSe.text = currentItem.shareCount.toString()
        holder.txtSoLuongBinhLuan.text = currentItem.commentCount.toString()

        val content = currentItem.content
        val wordCount = content.trim().split("\\s+".toRegex()).size

        if (wordCount > 100) {
            holder.txtXemThem.visibility = View.VISIBLE
            if (currentItem.isExpanded) {
                holder.txtNoiDung.text = content
                holder.txtXemThem.text = "Thu gọn"
            } else {
                holder.txtNoiDung.text = getShortContent(content)
                holder.txtXemThem.text = "Xem thêm"
            }
        } else {
            holder.txtNoiDung.text = content
            holder.txtXemThem.visibility = View.GONE
        }

        // bindingAdapterPosition: lấy vị trí item hiện tại khi click
        holder.txtXemThem.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                list[pos].isExpanded = !list[pos].isExpanded
                notifyItemChanged(pos)
            }
        }

        if (currentItem.is_liked) {
            holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
            holder.imgThich.setColorFilter(Color.parseColor("#FF0000"))
        } else {
            holder.imgThich.setImageResource(R.drawable.baseline_favorite_24)
            holder.imgThich.setColorFilter(Color.parseColor("#FFFFFFFF"))
        }

        holder.imgThich.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val itemClick = list[pos]

                if (itemClick.is_liked) {
                    itemClick.is_liked = false
                    itemClick.likeCount = itemClick.likeCount - 1
                    holder.imgThich.setColorFilter(Color.parseColor("#FFFFFFFF"))
                    holder.txtSoLuongThich.text = itemClick.likeCount.toString()
                    onLikeClick(itemClick, false)
                } else {
                    itemClick.is_liked = true
                    itemClick.likeCount = itemClick.likeCount + 1
                    holder.imgThich.setColorFilter(Color.parseColor("#FF0000"))
                    holder.txtSoLuongThich.text = itemClick.likeCount.toString()
                    onLikeClick(itemClick, true)
                }
            }
        }

        val listMedia = currentItem.PostMedia
        if (listMedia.isNotEmpty()) {
            holder.revHienBaiDang.visibility = View.VISIBLE

            val layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.revHienBaiDang.layoutManager = layoutManager
            holder.revHienBaiDang.adapter = AdapterImage(listMedia)


            //Setup SnapHelper (Lướt từng ảnh)
            val snapHelper = PagerSnapHelper()
            holder.revHienBaiDang.onFlingListener = null // Tránh lỗi "Instance already set"
            snapHelper.attachToRecyclerView(holder.revHienBaiDang) //Gắn PagerSnapHelper vào RecyclerView

            val totalImages = listMedia.size
            if (totalImages > 1) {
                holder.txtIndicator.visibility = View.VISIBLE
                holder.txtIndicator.text = "1/$totalImages"

                holder.revHienBaiDang.clearOnScrollListeners()
                holder.revHienBaiDang.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            val centerView = snapHelper.findSnapView(layoutManager)
                            if (centerView != null) {
                                val posInSubList = layoutManager.getPosition(centerView)
                                holder.txtIndicator.text = "${posInSubList + 1}/$totalImages"
                            }
                        }
                    }
                })
            } else {
                holder.txtIndicator.visibility = View.GONE
                holder.revHienBaiDang.clearOnScrollListeners()//gỡ bỏ tất cả các sự kiện lắng nghe việc cuộn
            }
        } else {
            holder.revHienBaiDang.visibility = View.GONE
            holder.txtIndicator.visibility = View.GONE
        }


        holder.imgBinhLuan.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onCommentClick(list[pos])
        }

        holder.imgReport.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onReportClick(list[pos])
        }

        holder.imgChiaSe.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onShareClick(list[pos])
        }

        if (currentItem.User.avatar.toString().isNotEmpty()) {
            val fullUrl = "http://10.0.2.2:8989/api/images/${currentItem.User.avatar}"
            holder.imgDaiDien.load(fullUrl) {
                crossfade(true)
                error(R.drawable.profile)
                placeholder(R.drawable.profile)
            }
        } else {
            holder.imgDaiDien.load(R.drawable.profile)
        }
        holder.imgDaiDien.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onImageClick(list[pos].User.id.toString())

            }
        }



    }

    override fun getItemCount(): Int {
        return list.size
    }
    class UserProfileViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
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
        val txtXemThem: TextView = itemView.findViewById(R.id.txtXemThem)
        var txtIndicator: TextView = itemView.findViewById(R.id.txtIndicator)
    }
    fun setData(newList: List<PostModel>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }
    fun updateData(newList: List<PostModel>) {
        this.list.addAll(newList)
        notifyDataSetChanged()
    }
    private fun getShortContent(text: String, maxWords: Int = 100): String {
        val words = text.trim().split("\\s+".toRegex())
        return words.take(maxWords).joinToString(" ") + "..."
    }


}