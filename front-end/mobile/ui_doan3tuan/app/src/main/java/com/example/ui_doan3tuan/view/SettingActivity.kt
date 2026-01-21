package com.example.ui_doan3tuan.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.session.SessionManager
import com.example.ui_doan3tuan.viewmodel.LogoutViewModel
import com.example.ui_doan3tuan.viewmodel.NewsletterViewModel
import kotlin.getValue

class SettingActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var  userName: String
    private lateinit var  avatar: String
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var btnDangXuat: Button
    private lateinit var txtNamSetting: TextView
    private lateinit var imgThoatCaiDat: ImageView
    private lateinit var imgAvatarSetting: ImageView
    private val viewModel: LogoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        initViews()
        sessionManager = SessionManager(applicationContext)
        userName = sessionManager.getUser()?.full_name.toString()
        avatar = sessionManager.getUser()?.avatar.toString()
        Log.d("User", "$userName")
        Log.d("User", "avatar: $avatar")
        txtNamSetting.setText(userName)
        setupListener()
        setupObserve()
    }
    override fun onResume(){
        super.onResume()
        loadData()

    }
    private fun initViews(){
        btnDangXuat = findViewById(R.id.btnDangXuat)
        imgThoatCaiDat = findViewById(R.id.imgThoatCaiDat)
        txtNamSetting = findViewById(R.id.txtNamSetting)
        imgAvatarSetting = findViewById(R.id.imgAvatarSetting)

    }
    private fun loadData(){
        if(avatar==null || avatar == ""){
            imgAvatarSetting.load(R.drawable.profile)
        }else{
            imgAvatarSetting.load("http://10.0.2.2:8989/api/images/$avatar"){
                placeholder(R.drawable.profile)
                error(R.drawable.profile)
                crossfade(true)
            }

        }
    }
    private fun setupObserve(){
        viewModel.logout.observe(this) { isSuccess ->
            if (isSuccess) {
                sessionManager.clearSession()
                val logout = Intent(this, LoginActivity::class.java)
                logout.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(logout)
                finish()
            }
        }
    }
    private fun setupListener(){
        btnDangXuat.setOnClickListener {
            accessToken = sessionManager.getAccessToken().toString()
            refreshToken = sessionManager.getRefreshToken().toString()
            Log.d("User", "$accessToken")
            Log.d("User", "$refreshToken")
            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                viewModel.logout(accessToken, refreshToken)
            }
        }
        imgThoatCaiDat.setOnClickListener {
            finish()
        }
    }
}