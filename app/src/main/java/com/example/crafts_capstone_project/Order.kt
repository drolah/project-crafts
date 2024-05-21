package com.example.crafts_capstone_project

data class Order(
    val username: String = "",
    val email: String = "",
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val total: String = "",
    val imageResource: Int = 0
) : java.io.Serializable
