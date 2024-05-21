package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MessageActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message)
        val chtButton = findViewById<ImageButton>(R.id.i_chat)
        chtButton.setOnClickListener {
            intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        val notif = findViewById<ImageButton>(R.id.i_notif)
        notif.setOnClickListener {
            intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        val orderBtn = findViewById<ImageButton>(R.id.i_orders)
        orderBtn.setOnClickListener {
            intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }


        val homeBtn = findViewById<ImageView>(R.id.m_home)
        val dBtn = findViewById<ImageView>(R.id.m_design)
        val cartBtn = findViewById<ImageView>(R.id.m_cart)
        val userBtn = findViewById<ImageView>(R.id.m_account)

        cartBtn.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        dBtn.setOnClickListener{
            val intent = Intent(this, ToolsActivity::class.java)
            startActivity(intent)
        }

        homeBtn.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        userBtn.setOnClickListener{
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
    }
}