package com.example.crafts_capstone_project.data

data class Order(
    val username: String = "",
    val email: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val total: Double = 0.0,
    val storeName: String = "",
    val storeEmail: String = "",
    val image: String = ""
) : java.io.Serializable
