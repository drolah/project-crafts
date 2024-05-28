package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var carts: MutableList<Cart>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private lateinit var checkBoxAll: CheckBox
    private lateinit var subTotalTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var checkOutBtn: TextView
    private lateinit var shippingFee: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        // Set click listeners for navigation
        val account = findViewById<LinearLayout>(R.id.accounts)
        account.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        val design = findViewById<LinearLayout>(R.id.designs)
        design.setOnClickListener {
            val intent = Intent(this, ToolsActivity::class.java)
            startActivity(intent)
        }

        val home = findViewById<LinearLayout>(R.id.homes)
        home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val messages = findViewById<LinearLayout>(R.id.messages)
        messages.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        checkBoxAll = findViewById(R.id.checkBoxAll)
        subTotalTextView = findViewById(R.id.subTotal)
        totalTextView = findViewById(R.id.total)
        shippingFee = findViewById(R.id.shippingFee)
        checkOutBtn = findViewById(R.id.checkOutAllBtn)


        // Check if the user is logged in
        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        carts = mutableListOf()
        cartAdapter = CartAdapter(carts) { isChecked, cart ->
            updateTotalFees()
            updateCheckAllState()
        }
        recyclerView.adapter = cartAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("carts")

        // Fetch cart items
        fetchCart(email)

        checkBoxAll.setOnCheckedChangeListener { _, isChecked ->
            if (!checkBoxAll.isPressed) return@setOnCheckedChangeListener // Ensure this was triggered by user interaction
            carts.forEach { it.isSelected = isChecked }
            cartAdapter.notifyDataSetChanged()
            updateTotalFees()
        }
        // Set checkout button click listener
        checkOutBtn.setOnClickListener {
            handleCheckout()
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCart(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    carts.clear()
                    // Check if there are any cart items
                    if (snapshot.exists()) {
                        // Iterate through each cart item and add it to the list
                        for (cartSnapshot in snapshot.children) {
                            val cart = cartSnapshot.getValue(Cart::class.java)
                            cart?.let { carts.add(it) }
                        }
                        // Notify the adapter that the data set has changed
                        cartAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@CartActivity, "No items in the cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@CartActivity, "Failed to load carts: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun updateTotalFees() {
        var subTotal = 0.0
        var total = 0.0
        var shipFee = 110.0
        carts.forEach { cart ->
            if (cart.isSelected) {
                subTotal += cart.price.toDouble() * cart.quantity.toDouble()
            }
        }
        total = subTotal+shipFee // Adjust this if you have additional fees like shipping

        subTotalTextView.text = subTotal.toString()
        totalTextView.text = total.toString()
        shippingFee.text = shipFee.toString()
        checkOutBtn.text = "Check out(${carts.count { it.isSelected }})"
    }

    private fun updateCheckAllState() {
        checkBoxAll.isChecked = carts.all { it.isSelected }
    }

    private fun handleCheckout() {
        val selectedItems = carts.filter { it.isSelected }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show()
            return
        }

        // Save selected items to Firebase
        saveSelectedItemsToDatabase(selectedItems)
    }

    private fun saveSelectedItemsToDatabase(selectedItems: List<Cart>) {
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        if (username.isNullOrEmpty() || email.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a reference to the orders node in Firebase
        val ordersReference = FirebaseDatabase.getInstance().getReference("orders")

        selectedItems.forEach { cartItem ->
            val order = Order(
                username = username,
                email = email,
                name = cartItem.name,
                price = cartItem.price,
                quantity = cartItem.quantity,
                total = (cartItem.price * cartItem.quantity),
                image = cartItem.image
            )

            ordersReference.push().setValue(order)
                .addOnSuccessListener {
                    Toast.makeText(this, "Order placed for ${cartItem.name}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to place order for ${cartItem.name}: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}