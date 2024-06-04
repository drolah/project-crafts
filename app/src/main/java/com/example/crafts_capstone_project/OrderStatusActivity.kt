package com.example.crafts_capstone_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.adapter.OrderStatusAdapter
import com.example.crafts_capstone_project.data.OrderStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderStatusActivity : AppCompatActivity() {

    private lateinit var orderStatusAdapter: OrderStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_status)

        val backButton = findViewById<ImageView>(R.id.orderStatus)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewStatus)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderStatusAdapter = OrderStatusAdapter(emptyList())
        recyclerView.adapter = orderStatusAdapter


        val orderId = intent.getStringExtra("orderId")
        if (orderId != null) {
            fetchOrderStatuses(orderId)
        } else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchOrderStatuses(orderId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("orderStatus").child(orderId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderStatuses = mutableListOf<OrderStatus>()
                for (childSnapshot in snapshot.children) {
                    val orderStatus = childSnapshot.getValue(OrderStatus::class.java)
                    if (orderStatus != null) {
                        orderStatuses.add(orderStatus)
                    }
                }
                if (orderStatuses.isNotEmpty()) {
                    orderStatusAdapter.updateOrderStatuses(orderStatuses)
                } else {
                    Toast.makeText(this@OrderStatusActivity, "No data found for this order ID", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderStatusActivity, "Failed to load order statuses", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
