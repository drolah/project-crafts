package com.example.crafts_capstone_project


data class User(
    val username: String = "",
    val email: String = "",
    val isOnline: Boolean = false
) : java.io.Serializable
