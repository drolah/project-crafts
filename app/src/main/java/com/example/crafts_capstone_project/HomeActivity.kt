package com.example.crafts_capstone_project

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var storeAdapter: StoreAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val account = findViewById<ImageView>(R.id.account)
        val dBtn = findViewById<ImageView>(R.id.design)
        val cartBtn = findViewById<ImageView>(R.id.cart)
        val messageBtn = findViewById<ImageView>(R.id.messages)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)


        storeRecyclerView = findViewById(R.id.storeRecyclerView)
        storeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        storeAdapter = StoreAdapter(userList, this)
        storeRecyclerView.adapter = storeAdapter

        fetchStoresFromFirebase()


        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null || username.isEmpty()) {
            // Username doesn't exist or is empty, redirect to login page
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        account.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        messageBtn.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        cartBtn.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        dBtn.setOnClickListener{
            val intent = Intent(this, ToolsActivity::class.java)
            startActivity(intent)
        }

        // Fetch data from Firebase Database
        fetchProductsFromFirebase()


        val searchView = findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchProduct(it.capitalize()) }
                return true
            }
        })
    }

    private fun fetchProductsFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                // Update RecyclerView with fetched data
                productAdapter = ProductAdapter(productList)
                recyclerView.adapter = productAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Database Error: ${databaseError.message}")
            }
        })
    }

    private fun fetchStoresFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                storeAdapter = StoreAdapter(userList, this@HomeActivity)
                storeRecyclerView.adapter = storeAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MainActivity", "Database Error: ${databaseError.message}")
            }
        })
    }

    private fun searchProduct(query: String) {
        val queryRef = databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff")
        queryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val searchResult = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { searchResult.add(it) }
                }
                // Update RecyclerView with search results
                productAdapter.updateData(searchResult)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Database Error: ${databaseError.message}")
            }
        })
    }

}