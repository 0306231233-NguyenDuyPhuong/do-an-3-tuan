package com.example.ui_doan3tuan.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterSelectImageAndVideo

class CreatePostActivity : AppCompatActivity() {
    private lateinit var adapterChonAnhVideo: AdapterSelectImageAndVideo


    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
            if (uris.isNotEmpty()) {
                // Lấy danh sách đang có, gộp với danh sách mới và loại bỏ trùng lặp (distinct)
                val currentList = adapterChonAnhVideo.currentList
                val updatedList = (currentList + uris).distinct()

                adapterChonAnhVideo.submitList(updatedList)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)

        adapterChonAnhVideo = AdapterSelectImageAndVideo { uriToDelete ->
            // Khi người dùng nhấn vào ảnh, logic này sẽ chạy:
            val newList = adapterChonAnhVideo.currentList.toMutableList()
            newList.remove(uriToDelete)
            adapterChonAnhVideo.submitList(newList)
        }

        setupDropdownStatus()

        val revHienThiAnhView = findViewById<RecyclerView>(R.id.revHienAnhVideo)
        revHienThiAnhView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        revHienThiAnhView.adapter = adapterChonAnhVideo


        findViewById<Button>(R.id.btnChonAnhVideo).setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }


        findViewById<Button>(R.id.btnDangBai).setOnClickListener {
            val finalImages = adapterChonAnhVideo.currentList
            startActivity(Intent(this, NewsletterActivity::class.java))

            finish()
        }


        findViewById<ImageView>(R.id.imgThoatTaoBaiDang).setOnClickListener {
            finish()
        }
    }

    private fun setupDropdownStatus() {
        val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdownStatus)
        val list = listOf("Công khai", "Bạn bè", "Chỉ mình tôi")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        dropdown.setAdapter(adapter)

        dropdown.setOnItemClickListener { _, _, position, _ ->
            val iconRes = when (position) {
                0 -> R.drawable.baseline_public_24
                1 -> R.drawable.outline_group_24
                2 -> R.drawable.outline_lock_24
                else -> R.drawable.baseline_public_24
            }
            dropdown.setCompoundDrawablesRelativeWithIntrinsicBounds(iconRes, 0, 0, 0)
        }
    }
}