package com.example.crafts_capstone_project.data

data class OrderStatus(
    val orderId: String,
    val productName: String,
    val status: String,
    val quantityShipped: Int,
    val quantityReceived: Int,
    val quantityReturned: Int,
    val totalAmount: Double
)

