package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.crafts_capstone_project.fragments.AccountFragment
import com.example.crafts_capstone_project.fragments.CartFragment
import com.example.crafts_capstone_project.fragments.HomeFragment
import com.example.crafts_capstone_project.fragments.MessageFragment

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