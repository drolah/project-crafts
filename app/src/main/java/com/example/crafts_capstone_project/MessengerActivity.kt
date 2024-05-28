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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger)
        senderUsername = intent.getStringExtra("senderUsername") ?: ""
        senderEmail = intent.getStringExtra("senderEmail") ?: ""
        receiverUsername = intent.getStringExtra("receiverUsername") ?: ""
        receiverEmail = intent.getStringExtra("receiverEmail") ?: ""

        database = FirebaseDatabase.getInstance().reference.child("messages")

        val receiverName = findViewById<TextView>(R.id.receiverName)

        receiverName.text = receiverUsername

        messageInput = findViewById(R.id.messageInput)
        val sendButton: ImageView = findViewById(R.id.sendButton)
        messagesRecyclerView = findViewById(R.id.messages)

        messageAdapter = MessageAdapter(messages, senderEmail)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter

        loadMessages()

        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                messageInput.setText("")
            }
        }
    }

    private fun sendMessage(messageContent: String) {
        val timestamp = System.currentTimeMillis()
        val messageId = UUID.randomUUID().toString()

        val message = Message(
            senderUsername,
            senderEmail,
            receiverUsername,
            receiverEmail,
            messageContent,
            timestamp
        )

        database.child(messageId).setValue(message)
            .addOnSuccessListener {
                messages.add(message)
                messageAdapter.notifyItemInserted(messages.size - 1)
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMessages() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    messages.add(message)
                    messageAdapter.notifyItemInserted(messages.size - 1)
                    messagesRecyclerView.scrollToPosition(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}