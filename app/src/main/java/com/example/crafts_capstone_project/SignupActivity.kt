package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

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

            // Sign up user with Firebase Authentication
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        userId?.let {
                            // Save user data to Realtime Database
                            val userRef = database.getReference("users").child(it)
                            val userData = HashMap<String, Any>()
                            userData["username"] = usernameText
                            userData["email"] = emailText
                            userData["isOnline"] = false
                            userRef.setValue(userData)

                            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Failed to create account!", Toast.LENGTH_SHORT).show()
                    }
                }
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