package com.example.crafts_capstone_project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private var productList: List<Product>,
    private val listener: OnImageLoadingCompleteListener // Callback interface
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_display, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product, position, itemCount) // Pass position and itemCount to the ViewHolder
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDisplayActivity::class.java)
            intent.putExtra("product", product)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.image)
        private val itemName: TextView = itemView.findViewById(R.id.name)
        private val itemPrice: TextView = itemView.findViewById(R.id.price)
        private val storeName: TextView = itemView.findViewById(R.id.userName)

        fun bind(product: Product, position: Int, itemCount: Int) {
            storeName.text = product.userName
            itemName.text = product.productName
            itemPrice.text = "Php ${product.price}"
            ImageDownloaderTask(itemImage, object : ImageDownloaderTask.ImageLoaderListener {
                override fun onImageLoaded() {
                    if (position == itemCount - 1) { // Last item
                        listener.onAllImagesLoaded() // Notify when all images are loaded
                    }
                }
            }).execute(product.image)
        }
    }

    fun updateData(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    // Callback interface for image loading completion
    interface OnImageLoadingCompleteListener {
        fun onAllImagesLoaded()
    }
}