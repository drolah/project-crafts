package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MessengerActivity : AppCompatActivity() {
    private lateinit var messageContainer: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "MessengerPrefs"
        const val MESSAGES_KEY = "Messages"
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger)
        val backBtn = findViewById<ImageButton>(R.id.back)
        backBtn.setOnClickListener {
            intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        messageContainer = findViewById(R.id.messageContainer)
        messageInput = findViewById(R.id.messageInput)
        val sendButton: ImageView = findViewById(R.id.sendButton)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load saved messages
        loadMessages()

        // Send button click listener
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addMessageToView(message)
                saveMessage(message)
                messageInput.setText("")
            }
        }
    }

    private fun addMessageToView(message: String) {
        val textView = TextView(this).apply {
            text = message
            setPadding(16, 8, 16, 8)
            setBackgroundResource(R.drawable.shape_input) // Create a drawable for message background
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
                gravity = Gravity.END // Align the message to the right
            }
        }
        messageContainer.addView(textView)

        // Scroll to the bottom
        val scrollView: ScrollView = findViewById(R.id.content)
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun saveMessage(message: String) {
        val messages = sharedPreferences.getStringSet(MESSAGES_KEY, mutableSetOf())?.toMutableSet()
        messages?.add(message)
        sharedPreferences.edit().putStringSet(MESSAGES_KEY, messages).apply()
    }

    private fun loadMessages() {
        val messages = sharedPreferences.getStringSet(MESSAGES_KEY, mutableSetOf())
        messages?.forEach { addMessageToView(it) }
    }
}