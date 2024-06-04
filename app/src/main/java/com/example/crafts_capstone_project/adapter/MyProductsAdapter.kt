package com.example.crafts_capstone_project.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.ImageDownloaderTask
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.UpdateProductActivity
import com.example.crafts_capstone_project.data.Product
import com.google.firebase.database.FirebaseDatabase

class MyProductsAdapter(private val context: Context, private val products: MutableList<Product>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (products.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_PRODUCT) {
            val view = inflater.inflate(R.layout.seller_product_layout, parent, false)
            ProductViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.empty_cart_layout, parent, false)
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
                updateProduct.setOnClickListener {
                    updateProduct(product)
                }

                deleteProduct.setOnClickListener {
                    deleteProduct(product)
                }
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
        val updateProduct: Button = itemView.findViewById(R.id.update_prod_btn)
        val deleteProduct: Button = itemView.findViewById(R.id.delete_prod_btn)
    }

    private fun deleteProduct(product: Product) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("products")
        val query = databaseReference.orderByChild("productName").equalTo(product.productName)

        query.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                        products.remove(product)
                        notifyDataSetChanged()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to query product", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProduct(product: Product) {
        val intent = Intent(context, UpdateProductActivity::class.java).apply {
            putExtra("productName", product.productName)
            putExtra("productPrice", product.price)
            putExtra("productImage", product.image)
        }
        context.startActivity(intent)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
