package com.example.ui_doan3tuan.view

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui_doan3tuan.R
import com.google.android.material.snackbar.Snackbar

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<TextView>(R.id.txtHuy).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn có muốn hủy bỏ chỉnh sửa không?")
                .setPositiveButton("Có") { d, _ ->
                    finish()
                    d.dismiss()
                }
                .show()


        }
        findViewById<TextView>(R.id.txtLuu).setOnClickListener {
            Snackbar.make(it,"Lưu thành công",Snackbar.LENGTH_SHORT).show();
            finish()
        }

    }
}