package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductDisplayActivity : AppCompatActivity() {
    private var soldProduct = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_display)
        val product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            val imageView = findViewById<ImageView>(R.id.image)
            val priceTextView = findViewById<TextView>(R.id.price)
            val nameTextView = findViewById<TextView>(R.id.name)
            val storeName = findViewById<TextView>(R.id.storeName)

            storeName.text = product.userName
            ImageDownloaderTask(imageView).execute(product.image)
            priceTextView.text = "Php ${product.price}"
            nameTextView.text = product.productName
        }

        val pdBack = findViewById<ImageButton>(R.id.pd_bck_btn)

        pdBack.setOnClickListener {
            onBackPressed()
        }

        loadSoldProduct()

        val choose = findViewById<Button>(R.id.choose)
        choose.setOnClickListener{
            val intent = Intent(this, ProductQtyActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val num = data?.getIntExtra("quantity", 0) ?: 0
            soldProduct += num
            savedSoldProduct()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveSoldProduct() {
        val sharedPref = getSharedPreferences("product_display", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt("sold_product", soldProduct)
            apply()
        }
    }

    private fun loadSoldProduct() {
        val sharedPref = getSharedPreferences("product_display", Context.MODE_PRIVATE)
        soldProduct = sharedPref.getInt("sold_product", 0)
    }

    private fun savedSoldProduct() {
        saveSoldProduct()
    }
}