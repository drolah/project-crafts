package com.example.crafts_capstone_project

data class Cart(
    val username: String = "",
    val email: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val total: Double = 0.0,
    val image: String = "",
    var isSelected: Boolean = false,

    val storeName: String = "",
    val storeEmail: String = "",
) : java.io.Serializable
