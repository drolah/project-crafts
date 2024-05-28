package com.example.crafts_capstone_project

import java.io.Serializable

data class Message(
    val senderUsername: String,
    val senderEmail: String,
    val receiverUsername: String,
    val receiverEmail: String,
    val message: String,
    val timestamp: Long
) : Serializable