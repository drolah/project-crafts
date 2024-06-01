package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal
import java.math.RoundingMode

class CartFragment : Fragment() {
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

    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        checkBoxAll = view.findViewById(R.id.checkBoxAll)
        subTotalTextView = view.findViewById(R.id.subTotal)
        totalTextView = view.findViewById(R.id.total)
        shippingFee = view.findViewById(R.id.shippingFee)
        checkOutBtn = view.findViewById(R.id.checkOutAllBtn)
        progressBar = view.findViewById(R.id.progressBar)

        // Check if the user is logged in
        if (email == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return view
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
            val intent = Intent(requireContext(), OrderActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchCart(email: String) {
        progressBar.visibility = View.VISIBLE
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
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "No items in the cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to load carts: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateTotalFees() {
        var subTotal = 0.0
        var total = 0.0
        val shipFee = 110.0
        carts.forEach { cart ->
            if (cart.isSelected) {
                subTotal += cart.price.toDouble() * cart.quantity.toDouble()
            }
        }
        total = subTotal + shipFee // Adjust this if you have additional fees like shipping

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
            Toast.makeText(requireContext(), "No items selected for checkout", Toast.LENGTH_SHORT).show()
            return
        }

        // Save selected items to Firebase
        saveSelectedItemsToDatabase(selectedItems)
    }

    private fun saveSelectedItemsToDatabase(selectedItems: List<Cart>) {
        val username = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        if (username.isNullOrEmpty() || email.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
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
                image = cartItem.image,
                storeName = cartItem.storeName,
                storeEmail = cartItem.storeEmail
            )

            ordersReference.push().setValue(order)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Order placed for ${cartItem.name}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to place order for ${cartItem.name}: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}