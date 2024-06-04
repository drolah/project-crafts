package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crafts_capstone_project.databinding.ActivitySignupBinding
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userRef: DatabaseReference
    private var userId: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val login = findViewById<TextView>(R.id.login)
        val signup = findViewById<Button>(R.id.signup)
        val username = findViewById<EditText>(R.id.user)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val repeatPass = findViewById<EditText>(R.id.repeatPassword)

        login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        signup.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val repeatPassword = repeatPass.text.toString()
            val usernameText = username.text.toString()

            // Validate email, username, and password fields (basic checks)
            if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(repeatPassword) || TextUtils.isEmpty(usernameText)) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(emailText)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordText != repeatPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isSecurePassword(passwordText)) {
                Toast.makeText(this, "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            database.reference.child("users").orderByChild("username").equalTo(usernameText)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(this@SignupActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.createUserWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener(this@SignupActivity) { task ->
                                    if (task.isSuccessful) {
                                        userId = auth.currentUser!!.uid
                                        // Save user data to Realtime Database
                                        userRef = database.reference.child("users").child(userId)
                                        val userData = HashMap<String, Any>()
                                        userData["userId"] = userId
                                        userData["username"] = usernameText
                                        userData["email"] = emailText
                                        userData["isOnline"] = false
                                        userData["profile"] = "https://firebasestorage.googleapis.com/v0/b/it-sysarch32-78625-activ-e8685.appspot.com/o/partialImage%2Fuser-profile-icon-vector-avatar-600nw-2247726673.webp?alt=media&token=ef4828bb-7d33-4881-b337-d13e98b0f832"
                                        userData["search"] = usernameText.lowercase()
                                        userRef.updateChildren(userData).addOnCompleteListener { saveTask ->
                                            if (saveTask.isSuccessful) {
                                                Toast.makeText(this@SignupActivity, "Sign up successful", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this@SignupActivity, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this@SignupActivity, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@SignupActivity, "Failed to create account!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SignupActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isSecurePassword(password: String): Boolean {
        val pattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        return pattern.matcher(password).matches()
    }
}
