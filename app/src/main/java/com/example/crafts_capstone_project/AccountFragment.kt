package com.example.crafts_capstone_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")
        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        val username = view.findViewById<TextView>(R.id.accountName)
        val user = sharedPreferences.getString("username", null)
        user?.let {
            username.text = user
        }

        val pay = view.findViewById<LinearLayout>(R.id.toPay)
        val ship = view.findViewById<LinearLayout>(R.id.toShip)
        val receive = view.findViewById<LinearLayout>(R.id.toRecieve)
        val returns = view.findViewById<LinearLayout>(R.id.returns)
        val sell = view.findViewById<TextView>(R.id.sell)

        sell.setOnClickListener {
            val intent = Intent(requireContext(), UploadProductActivity::class.java)
            startActivity(intent)
        }

        val settings = view.findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener {
            val options = arrayOf("Orders", "My Products", "Logout")

            val builder = AlertDialog.Builder(requireContext())

            // Set the dialog title
            builder.setTitle("Settings")

            // Set options as buttons
            builder.setItems(options) { dialog, which ->
                // Handle each option click
                when (which) {
                    0 -> {
                        val intent = Intent(requireContext(), SellersOrder::class.java)
                        startActivity(intent)
                    }

                    1 -> {
                        val intent = Intent(requireContext(), MyProducts::class.java)
                        startActivity(intent)
                    }

                    2 -> {
                        val userId = auth.currentUser?.uid
                        userId?.let { uid ->
                            // Set isOnline to false before logging out
                            database.child(uid).child("isOnline").setValue(false)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        // Clear SharedPreferences
                                        val editor = sharedPreferences.edit()
                                        editor.remove("username")
                                        editor.remove("email")
                                        editor.remove("password")
                                        editor.apply()

                                        // Logout and navigate to MainActivity
                                        auth.signOut()
                                        val intent = Intent(requireContext(), MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to update online status", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }
                dialog.dismiss() // Dismiss the dialog after an option is selected
            }

            // Create the AlertDialog
            val dialog: AlertDialog = builder.create()

            // Show the AlertDialog
            dialog.show()
        }

        pay.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatus::class.java)
            startActivity(intent)
        }

        ship.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatus::class.java)
            startActivity(intent)
        }

        receive.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatus::class.java)
            startActivity(intent)
        }

        returns.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatus::class.java)
            startActivity(intent)
        }

        return view
    }
}