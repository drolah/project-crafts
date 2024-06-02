package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.crafts_capstone_project.data.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class UploadProductActivity : AppCompatActivity() {
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var ivProductImage: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnUploadProduct: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    private var userName = ""
    private var userEmail = ""
    private lateinit var progressBar: ProgressBar

    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_product)
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val back = findViewById<TextView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }


        val user = sharedPreferences.getString("username", null)
        user?.let {
            userName = user
        }

        val email = sharedPreferences.getString("email", null)
        email?.let {
            userEmail = email
        }

        progressBar = findViewById(R.id.progressBar)
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        ivProductImage = findViewById(R.id.ivProductImage)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        btnUploadProduct = findViewById(R.id.btnUploadProduct)

        btnChooseImage.setOnClickListener {
            chooseImage()
        }

        btnUploadProduct.setOnClickListener {
            uploadProduct()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            ivProductImage.setImageURI(imageUri)
        }
    }

    private fun uploadProduct() {
            val productName = etProductName.text.toString().trim()
            val productPrice = etProductPrice.text.toString().trim().toDoubleOrNull()

            if (productName.isEmpty() || productPrice == null || imageUri == null) {
                // Handle error: show a message to the user
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            progressBar.visibility = View.VISIBLE

            // Upload image to Firebase Storage
            val imageRef = storageReference.child("product_images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Get the download URL for the uploaded image
                        val imageUrl = uri.toString()

                        // Create a new product object
                        val product = Product(
                            email = userEmail,
                            userName = userName,
                            productName = productName,
                            image = imageUrl,
                            price = productPrice
                        )

                        // Save product to Firebase Realtime Database
                        val productId = database.child("products").push().key
                        if (productId != null) {
                            database.child("products").child(productId).setValue(product)
                                .addOnSuccessListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                                    // Redirect to another activity if needed
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(this, "Failed to upload product", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
    }
}