package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChatActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        // Assuming `this` is the context (e.g., an activity or fragment)
        val buttons = arrayOf(
            findViewById<Button>(R.id.btns1),
            findViewById<Button>(R.id.btns2),
            findViewById<Button>(R.id.btns3),
            findViewById<Button>(R.id.btns4),
            findViewById<Button>(R.id.btns5),
            findViewById<Button>(R.id.btns6),
            findViewById<Button>(R.id.btns7),
            findViewById<Button>(R.id.btns8),
            findViewById<Button>(R.id.btns9),
            findViewById<Button>(R.id.btns10)
        )

        // Function to handle button clicks and start a new activity using Intent
        fun handleButtonClick(buttonIndex: Int) {
            // Create an Intent to start a new activity
            val intent = Intent(this, MessengerActivity::class.java)  // Replace `YourTargetActivity` with the target activity class

            // Add any additional data to the intent if needed
            intent.putExtra("buttonIndex", buttonIndex)  // Optional: Pass the button index to the new activity

            // Start the new activity
            startActivity(intent)
        }

// Set an OnClickListener for each button in the buttons array
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // Handle button click and start a new activity
                handleButtonClick(index)
            }
        }
    }
}