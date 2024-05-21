package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MessengerActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger)
        val backBtn = findViewById<ImageButton>(R.id.back)
        backBtn.setOnClickListener {
            intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}