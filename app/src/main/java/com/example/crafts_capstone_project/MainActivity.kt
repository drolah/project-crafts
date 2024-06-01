package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        if (isUserLoggedIn()) {
            // Start HomeActivity directly
            startHomeActivity()
            finish()
        }

        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val signup = findViewById<TextView>(R.id.signup)
        val email = findViewById<EditText>(R.id.email)

        login.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            // Validate email and password fields
            if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in user with Firebase Authentication
            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get user ID
                        val userId = auth.currentUser?.uid
                        // Retrieve username from Realtime Database
                        userId?.let { uid ->
                            database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val userData = dataSnapshot.value as Map<*, *>
                                        val username = userData["username"] as String
                                        val userInfo = dataSnapshot.getValue(Users::class.java)
                                        userInfo?.let {
                                            // Save username in SharedPreferences
                                            saveUsername(username, emailText, passwordText)

                                            database.child(uid).child("isOnline").setValue(true).addOnCompleteListener { updateTask ->
                                                if (updateTask.isSuccessful) {
                                                    // Start HomeActivity
                                                    isUserLoggedIn()
                                                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    Toast.makeText(this@MainActivity, "Failed to update online status", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        // If the user is not found in the database
                                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle error
                                    Toast.makeText(this@MainActivity, "Failed to login", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Login failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        signup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun isUserLoggedIn(): Boolean {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun saveUsername(username: String, email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }
}