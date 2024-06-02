package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.crafts_capstone_project.data.Cart
import com.example.crafts_capstone_project.data.Order
import com.example.crafts_capstone_project.data.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductQtyActivity : AppCompatActivity() {
    private lateinit var lessTv: TextView
    private lateinit var addTv: TextView
    private lateinit var prodQty: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var num = 1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_qty)
        lessTv = findViewById(R.id.lessTV)
        addTv = findViewById(R.id.addTV)
        prodQty = findViewById(R.id.pQty)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val product = intent.getSerializableExtra("product") as? Product
        if (product != null) {
            val imageView = findViewById<ImageView>(R.id.leather)
            val priceTextView = findViewById<TextView>(R.id.price)
            val nameTextView = findViewById<TextView>(R.id.desc)

            ImageDownloaderTask(imageView).execute(product.image)
            priceTextView.text = "Php ${product.price}"
            nameTextView.text = product.productName
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity if product is not found
            return
        }

        lessTv.setOnClickListener {
            decrementNumber()
        }

        addTv.setOnClickListener {
            incrementNumber()
        }

        val orderButton = findViewById<Button>(R.id.order)
        orderButton.setOnClickListener {
            placeOrder(product)
        }

        val addToCart = findViewById<Button>(R.id.addToCart)
        addToCart.setOnClickListener {
            saveToCart(product)
        }

        val pdBack = findViewById<ImageButton>(R.id.pd_bck_btn)
        pdBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun decrementNumber() {
        num--
        if (num < 1) {
            num = 1
        }
        updateProductQty()
    }

    private fun incrementNumber() {
        num++
        updateProductQty()
    }

    private fun updateProductQty() {
        prodQty.text = num.toString()
    }

    private fun placeOrder(product: Product?) {
        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            return
        }

        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("orders")

        if (username.isNullOrEmpty() || email.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = num
        val orderId = databaseReference.push().key ?: return
        val total = product.price * quantity.toDouble()

        val order = Order(
            orderId = orderId,
            username = username,
            email = email,
            name = product.productName,
            price = product.price.toDouble(),
            quantity = quantity,
            total = total,
            image = product.image,
            storeEmail = product.email,
            storeName = product.userName
        )

        databaseReference.child(orderId).setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order Successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Order Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveToCart(product: Product?) {
        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            return
        }

        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        if (username.isNullOrEmpty() || email.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("carts")

        val quantity = num
        val total = product.price * quantity.toDouble()

        val cart = Cart(
            username = username,
            email = email,
            name = product.productName,
            price = product.price.toDouble(),
            quantity = quantity,
            total = total,
            image = product.image,
            storeEmail = product.email,
            storeName = product.userName
        )

        databaseReference.push().setValue(cart)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added to cart!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add product to cart: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}