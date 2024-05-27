package com.example.crafts_capstone_project

data class Product(
    val email: String = "",
    val userName: String = "",
    val productName: String = "",
    val image: String = "",
    val stocks: Int = 0,
    val price: Double = 0.0
) : java.io.Serializable
