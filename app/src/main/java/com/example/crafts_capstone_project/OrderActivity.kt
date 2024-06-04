// OrderActivity.kt
package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.adapter.OrderAdapter
import com.example.crafts_capstone_project.adapter.OrderClickListener
import com.example.crafts_capstone_project.data.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderActivity : AppCompatActivity(), OrderClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orders: MutableList<Order>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orders = mutableListOf()
        orderAdapter = OrderAdapter(orders, this)
        recyclerView.adapter = orderAdapter

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("orders")

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        fetchOrders(email)
    }
    private fun fetchOrders(email: String) {
        progressBar.visibility = View.VISIBLE
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    orders.clear()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        order?.let { orders.add(it) }
                    }
                    orderAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    if (orders.isEmpty()) {
                        Toast.makeText(this@OrderActivity, "No orders found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@OrderActivity, "Failed to load orders: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onOrderClicked(order: Order) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("orderId", order.orderId)
        startActivity(intent)
    }
}
