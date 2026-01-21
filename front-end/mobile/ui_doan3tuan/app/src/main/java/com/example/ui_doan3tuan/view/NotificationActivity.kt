package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)

        initViews()
        setupBottomNav()

    }
    private fun initViews(){
        bottomNav = findViewById(R.id.bottom_navigation)
    }
    private fun setupBottomNav(){
        bottomNav.selectedItemId = R.id.nav_notification
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> navigateTo(NewsletterActivity::class.java)
                R.id.nav_friend -> navigateTo(FriendsListActivity::class.java)
                R.id.nav_add -> navigateTo(CreatePostActivity::class.java)
                R.id.nav_notification -> true
                R.id.nav_profile -> navigateTo(UserProfileActivity::class.java)
                else -> false
            }

        }

    }
    private fun navigateTo(cls:Class<*>): Boolean{
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        overridePendingTransition(0, 0)
        return false;
    }

}