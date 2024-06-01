package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.fragment.app.Fragment
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

    private lateinit var progress: ProgressBar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val homeBtn = findViewById<ImageView>(R.id.home)
        val dBtn = findViewById<ImageView>(R.id.design)
        val messages = findViewById<ImageView>(R.id.messages)
        val cart = findViewById<ImageView>(R.id.cart)
        val account = findViewById<ImageView>(R.id.account)

        fragmentDisplay(HomeFragment())

        homeBtn.setOnClickListener{
            val view = findViewById<ViewGroup>(R.id.containerFragment)
            view.removeAllViews()
            fragmentDisplay(HomeFragment())
        }

        account.setOnClickListener{
            val view = findViewById<ViewGroup>(R.id.containerFragment)
            view.removeAllViews()
            fragmentDisplay(AccountFragment())
        }

        cart.setOnClickListener{
            val view = findViewById<ViewGroup>(R.id.containerFragment)
            view.removeAllViews()
            fragmentDisplay(CartFragment())
        }

        messages.setOnClickListener{
            val view = findViewById<ViewGroup>(R.id.containerFragment)
            view.removeAllViews()
            fragmentDisplay(MessageFragment())
        }

        dBtn.setOnClickListener{
            val intent = Intent(this, ToolsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fragmentDisplay(fragment: Fragment){
        val fragmentShow = supportFragmentManager.beginTransaction()
        fragmentShow.replace(R.id.containerFragment, fragment)
        fragmentShow.commit()
    }
}