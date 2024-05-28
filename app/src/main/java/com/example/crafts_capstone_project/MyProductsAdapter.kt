package com.example.crafts_capstone_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyProductsAdapter(private val products: List<Product>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (products.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PRODUCT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.seller_product_layout, parent, false)
            ProductViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_cart_layout, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            val product = products[position]
            with(holder) {
                name.text = product.productName
                price.text = "P ${product.price}"
                ImageDownloaderTask(image).execute(product.image)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (products.isEmpty()) 1 else products.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}