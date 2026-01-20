package com.example.ui_doan3tuan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.view.CreatePostActivity
import com.example.ui_doan3tuan.view.FriendsListActivity
import com.example.ui_doan3tuan.view.NewsletterActivity
import com.example.ui_doan3tuan.view.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

    }

}