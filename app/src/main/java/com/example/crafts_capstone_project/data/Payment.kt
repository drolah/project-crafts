package com.example.crafts_capstone_project.data

data class Payment(
    val email: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = "",
    val proofUrl: String? = null
)
