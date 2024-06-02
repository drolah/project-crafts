package com.example.crafts_capstone_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.crafts_capstone_project.data.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class UpdateProductActivity : AppCompatActivity() {

    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var productImageIv: ImageView
    private lateinit var chooseImageBtn: Button
    private lateinit var btnUpdateProduct: Button
    private lateinit var progressBar: ProgressBar
    private val PICK_IMAGE_REQUEST = 71
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)

        productNameET = findViewById(R.id.productNameET)
        productPriceET = findViewById(R.id.productPriceET)
        productImageIv = findViewById(R.id.productImageIv)
        chooseImageBtn = findViewById(R.id.chooseImageBtn)
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct)
        progressBar = findViewById(R.id.progressBar)
        storageReference = FirebaseStorage.getInstance().reference
        database = FirebaseDatabase.getInstance().reference

        findViewById<TextView>(R.id.back).setOnClickListener {
            onBackPressed()
        }

        chooseImageBtn.setOnClickListener {
            chooseImage()
        }

        btnUpdateProduct.setOnClickListener {
            updateProductData()
        }

        val productName = intent.getStringExtra("productName")
        val productPrice = intent.getStringExtra("productPrice")
        val productImage = intent.getStringExtra("productImage")

        productNameET.setText(productName)
        productPriceET.setText(productPrice)
        Picasso.get().load(productImage).into(productImageIv)
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
            selectedImageUri = data.data
            productImageIv.setImageURI(selectedImageUri)
        }
    }

    private fun updateProductData() {
        val productName = productNameET.text.toString().trim()
        val productPriceStr = productPriceET.text.toString().trim().toDoubleOrNull()

        if (productName.isEmpty() || productPriceStr == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        if (selectedImageUri != null) {
            val imageRef = storageReference.child("product_images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveProductData(productName, productPriceStr, imageUrl)
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            val existingImageUrl = intent.getStringExtra("productImage") ?: ""
            saveProductData(productName, productPriceStr, existingImageUrl)
        }
    }

    private fun saveProductData(productName: String, productPrice: Double, imageUrl: String) {
        val productRef = database.child("products").orderByChild("productName").equalTo(productName)
        productRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.child("productName").setValue(productName)
                    snapshot.ref.child("price").setValue(productPrice)
                    snapshot.ref.child("image").setValue(imageUrl)
                }
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
        }
    }
}
