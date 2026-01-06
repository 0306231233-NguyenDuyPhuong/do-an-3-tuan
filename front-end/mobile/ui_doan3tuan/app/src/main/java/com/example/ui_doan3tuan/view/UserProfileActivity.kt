package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.imgSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        findViewById<ImageView>(R.id.imgThoatHoSoNguoiDung).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.btnChinhSuaTrangCaNhan).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }


    }
}