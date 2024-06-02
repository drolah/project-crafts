package com.example.crafts_capstone_project

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.adapter.OrderStatusAdapter
import com.example.crafts_capstone_project.data.OrderStatus

class OrderStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_status)
        val backButton = findViewById<ImageView>(R.id.orderStatus)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val orderId = intent.getStringExtra("orderId")
        val productName = intent.getStringExtra("productName")
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        if (orderId != null && productName != null) {
            val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStatus)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = OrderStatusAdapter(listOf(OrderStatus(orderId, productName, "", 0, 0, 0, totalAmount)))
        }
    }

}
