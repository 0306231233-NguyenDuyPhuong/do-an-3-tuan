package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ui_doan3tuan.R
import com.example.ui_doan3tuan.adapter.AdapterChonAnhVideo

class TaoBaiDangActivity : AppCompatActivity() {

    private var adapterChonAnhVideo = AdapterChonAnhVideo()

    private val pickMedia =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris ->
            if (uris.isNotEmpty()) {
                adapterChonAnhVideo.submitList(uris)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tao_bai_dang)
        val dropdown = findViewById<AutoCompleteTextView>(R.id.dropdownStatus)
        val list = listOf("Công khai", "Bạn bè", "Chỉ mình tôi")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            list //dữ liệu muốn hiện thị
        )
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


        val revHienThiAnhView = findViewById<RecyclerView>(R.id.revHienAnhVideo)
        revHienThiAnhView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        revHienThiAnhView.adapter = adapterChonAnhVideo
        // Nút nhấn thêm ảnh
        findViewById<Button>(R.id.btnChonAnhVideo).setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                )
            )
        }


    }
}