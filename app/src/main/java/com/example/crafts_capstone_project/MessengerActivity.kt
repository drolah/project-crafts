package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class MessengerActivity : AppCompatActivity() {
    private lateinit var messageInput: EditText
    private lateinit var senderUsername: String
    private lateinit var senderEmail: String
    private lateinit var receiverUsername: String
    private lateinit var receiverEmail: String
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesRecyclerView: RecyclerView
    private val messages = mutableListOf<Message>()
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger)
        senderUsername = intent.getStringExtra("senderUsername") ?: ""
        senderEmail = intent.getStringExtra("senderEmail") ?: ""
        receiverUsername = intent.getStringExtra("receiverUsername") ?: ""
        receiverEmail = intent.getStringExtra("receiverEmail") ?: ""

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        database = FirebaseDatabase.getInstance().reference.child("messages")

        val receiverName = findViewById<TextView>(R.id.receiverName)
        receiverName.text = receiverUsername

        val back = findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        messageInput = findViewById(R.id.messageInput)
        val sendButton: ImageView = findViewById(R.id.sendButton)
        messagesRecyclerView = findViewById(R.id.messages)


        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                messageInput.setText("")
            }
        }

        fetchMessages(senderEmail, receiverEmail)
    }

    private fun fetchMessages(senderEmail: String, receiverEmail: String) {
        val messagesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (childSnapshot in snapshot.children) {
                    val message = childSnapshot.getValue(Message::class.java)
                    if (message != null && ((message.senderEmail == senderEmail && message.receiverEmail == receiverEmail) ||
                                (message.senderEmail == receiverEmail && message.receiverEmail == senderEmail))) {
                        messages.add(message)
                    }
                }
                displayMessages(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessengerActivity, "Failed to read messages.", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(messagesListener)
    }

    private fun displayMessages(messages: List<Message>) {
        val email = sharedPreferences.getString("email", null)
        val adapter = MessageAdapter(messages, email!!)
        messagesRecyclerView.adapter = adapter
    }

    private fun sendMessage(messageContent: String) {
        val timestamp = System.currentTimeMillis()

        val message = Message(
            senderUsername,
            senderEmail,
            receiverUsername,
            receiverEmail,
            messageContent,
            timestamp
        )

        database.push().setValue(message)
        messageInput.text.clear()
    }
}