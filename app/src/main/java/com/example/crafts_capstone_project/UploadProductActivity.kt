package com.example.crafts_capstone_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UploadProductActivity : AppCompatActivity() {
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductStocks: EditText
    private lateinit var ivProductImage: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var btnUploadProduct: Button
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload_product)

        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductStocks = findViewById(R.id.etProductStocks)
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
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
        val productStocks = etProductStocks.text.toString().trim().toIntOrNull()


        if (productName.isEmpty() || productPrice == null || productStocks == null) {
            // Handle error: show a message to the user
            return
        }

        val intent = Intent(this, AccountActivity::class.java)
        startActivity(intent)
    }

}