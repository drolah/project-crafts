package com.example.crafts_capstone_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AccountActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val username = findViewById<TextView>(R.id.accountName)

        val user = sharedPreferences.getString("username", null)
        user?.let {
            username.text = user
        }


        val pay = findViewById<LinearLayout>(R.id.toPay)
        val ship = findViewById<LinearLayout>(R.id.toShip)
        val recieve = findViewById<LinearLayout>(R.id.toRecieve)
        val returns = findViewById<LinearLayout>(R.id.returns)

        val cart = findViewById<LinearLayout>(R.id.carts)
        cart.setOnClickListener{
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        val design = findViewById<LinearLayout>(R.id.designs)
        design.setOnClickListener{
            val intent = Intent(this, ToolsActivity::class.java)
            startActivity(intent)
        }

        val home = findViewById<LinearLayout>(R.id.homes)
        home.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val messages = findViewById<LinearLayout>(R.id.messages)
        messages.setOnClickListener{
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        val settings = findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener{
            val options = arrayOf("Report", "Chat", "Logout")

            val builder = AlertDialog.Builder(this)

            // Set the dialog title
            builder.setTitle("Settings")

            // Set options as buttons
            builder.setItems(options) { dialog, which ->
                // Handle each option click
                when (which) {
                    0 -> {
                        val intent = Intent(this, ReportActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this, MessageActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        val editor = sharedPreferences.edit()
                        editor.remove("username")
                        editor.remove("email")// Remove the username from SharedPreferences
                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                dialog.dismiss() // Dismiss the dialog after an option is selected
            }

            // Create the AlertDialog
            val dialog: AlertDialog = builder.create()

            // Show the AlertDialog
            dialog.show()
        }


        pay.setOnClickListener{
            val intent = Intent(this, OrderStatus::class.java)
            startActivity(intent)
        }

        ship.setOnClickListener{
            val intent = Intent(this, OrderStatus::class.java)
            startActivity(intent)
        }

        recieve.setOnClickListener{
            val intent = Intent(this, OrderStatus::class.java)
            startActivity(intent)
        }

        returns.setOnClickListener{
            val intent = Intent(this, OrderStatus::class.java)
            startActivity(intent)
        }
    }
}