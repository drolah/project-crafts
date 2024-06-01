package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.crafts_capstone_project.adapter.MyProductsAdapter
import com.example.crafts_capstone_project.data.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyProducts : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myProductsAdapter: MyProductsAdapter
    private lateinit var products: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_products)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.myProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("products")

        // Initialize products list
        products = mutableListOf()

        // Initialize ProductAdapter and set it to recyclerView
        myProductsAdapter = MyProductsAdapter(products)
        recyclerView.adapter = myProductsAdapter

        fetchMyProducts(email)
    }

    private fun fetchMyProducts(email: String) {
        progressBar.visibility = View.VISIBLE
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    products.clear()
                    // Iterate through each order snapshot and add it to the list
                    for (orderSnapshot in snapshot.children) {
                        val product = orderSnapshot.getValue(Product::class.java)
                        product?.let { products.add(it) }
                    }
                    // Notify the adapter that data set has changed
                    myProductsAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MyProducts, "Failed to load products: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}