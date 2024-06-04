package com.example.crafts_capstone_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.data.Order
import com.example.crafts_capstone_project.data.Payment
import com.example.crafts_capstone_project.data.OrderStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PaymentActivity : AppCompatActivity() {
    private lateinit var order: Order
    private lateinit var backButton: ImageView
    private lateinit var proofImageView: ImageView
    private lateinit var chooseFileButton: Button
    private lateinit var confirmButton: Button
    private lateinit var paymentMethod: String
    private var proofImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        val orderId = intent.getStringExtra("orderId")
        if (orderId == null) {
            Toast.makeText(this, "No order found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        proofImageView = findViewById(R.id.proof)
        chooseFileButton = findViewById(R.id.proofFile)
        confirmButton = findViewById(R.id.confirmBtn)
        backButton = findViewById(R.id.back_payment)

        backButton.setOnClickListener {
            onBackPressed()
        }

        findViewById<TextView>(R.id.cod).setOnClickListener {
            paymentMethod = "Cash on Delivery"
            chooseFileButton.isEnabled = false
        }

        findViewById<TextView>(R.id.gcash).setOnClickListener {
            paymentMethod = "GCash"
            chooseFileButton.isEnabled = true
        }

        chooseFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 1)
        }

        confirmButton.setOnClickListener {
            if (paymentMethod == "GCash" && proofImageUri == null) {
                Toast.makeText(this, "Please upload proof of payment", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            savePaymentDetails(orderId)
        }

        fetchOrderDetails(orderId)
    }

    private fun populateOrderDetails() {
        findViewById<TextView>(R.id.payment_name).text = order.name ?: ""
        findViewById<TextView>(R.id.payment_price).text = "P ${String.format("%.2f", order.price)}"
        findViewById<TextView>(R.id.payment_qty).text = order.quantity.toString()
        findViewById<TextView>(R.id.payment_total).text = "P ${String.format("%.2f", order.total)}"

        val paymentImageView = findViewById<ImageView>(R.id.payment_image)
        Picasso.get()
            .load(order.image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.box)
            .into(paymentImageView)
    }

    private fun fetchOrderDetails(orderId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("orders").child(orderId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    order = snapshot.getValue(Order::class.java) ?: return
                    populateOrderDetails()
                } else {
                    Toast.makeText(this@PaymentActivity, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaymentActivity, "Failed to fetch order details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun savePaymentDetails(orderId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val paymentId = database.push().key ?: return
        val status = if (proofImageUri == null) "Not Paid" else "Paid"

        if (proofImageUri != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("payment_proofs/$paymentId")
            storageReference.putFile(proofImageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        savePaymentToDatabase(paymentId, uri.toString(), status, orderId)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload proof of payment", Toast.LENGTH_SHORT).show()
                }
        } else {
            savePaymentToDatabase(paymentId, null, status, orderId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            proofImageUri = data?.data
            proofImageView.setImageURI(proofImageUri)
        }
    }

    private fun savePaymentToDatabase(paymentId: String, proofUrl: String?, status: String, orderId: String) {
        val payment = Payment(
            email = order.email,
            productName = order.name,
            price = order.price,
            quantity = order.quantity,
            total = order.total,
            paymentMethod = paymentMethod,
            status = status,
            proofUrl = proofUrl
        )
        FirebaseDatabase.getInstance().reference.child("payments").child(paymentId).setValue(payment)
            .addOnSuccessListener {
                Toast.makeText(this, "Payment confirmed", Toast.LENGTH_SHORT).show()
                updateOrderStatus(orderId, status)
                saveOrderStatus(orderId, status, order)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to confirm payment", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("orders").child(orderId)
        databaseReference.child("status").setValue("Paid")
            .addOnSuccessListener {
                Toast.makeText(this, "Order status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update order status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveOrderStatus(orderId: String, status: String, order: Order) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("orderStatus").child(orderId)
        val newOrderStatusRef = databaseReference.push()
        val orderStatusId = newOrderStatusRef.key ?: ""

        val orderStatus = OrderStatus(
            orderStatusId = orderStatusId,
            orderId = orderId,
            productName = order.name,
            status = status,
            quantityShipped = 0,
            quantityReceived = 0,
            quantityReturned = 0,
            totalAmount = if (status == "Paid") 0.0 else order.total
        )

        newOrderStatusRef.setValue(orderStatus)
            .addOnSuccessListener {
                Toast.makeText(this@PaymentActivity, "Order status saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@PaymentActivity, "Failed to save order status", Toast.LENGTH_SHORT).show()
            }
    }

}