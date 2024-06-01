package com.example.crafts_capstone_project.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.MainActivity
import com.example.crafts_capstone_project.data.Product
import com.example.crafts_capstone_project.adapter.ProductAdapter
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.adapter.StoreAdapter
import com.example.crafts_capstone_project.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var storeAdapter: StoreAdapter
    private val userList = mutableListOf<User>()
    private var userEmail = ""
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        storeRecyclerView = view.findViewById(R.id.storeRecyclerView)
        storeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        storeAdapter = StoreAdapter(userList, requireContext())
        storeRecyclerView.adapter = storeAdapter

        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        userEmail = sharedPreferences.getString("email", null).toString()

        if (username == null || username.isEmpty()) {
            // Username doesn't exist or is empty, redirect to login page
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchProduct(it.capitalize()) }
                return true
            }
        })

        fetchStoresFromFirebase()
        fetchProductsFromFirebase()

        return view
    }

    private fun fetchProductsFromFirebase() {
        progressBar.visibility = View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let {
                        productList.add(it)
                    }
                }
                // Update RecyclerView with fetched data
                productAdapter = ProductAdapter(productList, object : ProductAdapter.OnImageLoadingCompleteListener {
                    override fun onAllImagesLoaded() {
                        progressBar.visibility = View.GONE // Hide progress bar when all images are loaded
                    }
                })
                recyclerView.adapter = productAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Database Error: ${databaseError.message}")
                progressBar.visibility = View.GONE // Hide progress bar on error
            }
        })
    }

    private fun fetchStoresFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        if (it.email != userEmail) {
                            userList.add(it)
                        }
                    }
                }
                storeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MainActivity", "Database Error: ${databaseError.message}")
            }
        })
    }

    private fun searchProduct(query: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        val queryRef = databaseReference.orderByChild("productName").startAt(query).endAt(query + "\uf8ff")
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
                Log.e(ContentValues.TAG, "Database Error: ${databaseError.message}")
            }
        })
    }
}