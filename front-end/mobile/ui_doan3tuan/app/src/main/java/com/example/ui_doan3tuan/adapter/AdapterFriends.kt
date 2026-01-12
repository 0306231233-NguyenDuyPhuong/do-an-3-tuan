package com.example.ui_doan3tuan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.model.UserModel

class AdapterFriends(private val listFriend:List<UserModel>): RecyclerView.Adapter<AdapterFriends.FriendsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_newsletter, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FriendsViewHolder,
        position: Int
    ) {
        holder.txtTenBaBe.text = listFriend[position].full_name
        val url ="http://10.0.2.2:8989/api/images/${listFriend[position].avatar}"
        holder.imgBanBe.load(url)

    }

    override fun getItemCount(): Int {
        return listFriend.size
    }

    class FriendsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imgBanBe = itemView.findViewById<ImageView>(R.id.imgBanBe)
        val txtTenBaBe = itemView.findViewById<TextView>(R.id.txtTenBaBe)

    }

}