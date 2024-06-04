package com.example.crafts_capstone_project.data

data class OrderStatus(
    val orderStatusId: String = "",
    val orderId: String = "",
    val productName: String = "",
    val status: String = "",
    val quantityShipped: Int = 0,
    val quantityReceived: Int = 0,
    val quantityReturned: Int = 0,
    val totalAmount: Double = 0.0
)


