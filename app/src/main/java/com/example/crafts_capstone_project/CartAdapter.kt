package com.example.crafts_capstone_project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.material3.Checkbox
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val carts: List<Cart>,
    private val onItemCheckedChange: (Boolean, Cart) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CART = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (carts.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_CART
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_CART) {
            val view = inflater.inflate(R.layout.cart_layout, parent, false)
            CartViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.empty_cart_layout, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartViewHolder) {
            val cart = carts[position]
            holder.name.text = cart.name
            holder.price.text = "P ${cart.price}"
            holder.qty.text = cart.quantity
            holder.image.setImageResource(cart.imageResource) // Set the image resource
            holder.checkBox.isChecked = cart.isSelected

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                cart.isSelected = isChecked
                onItemCheckedChange(isChecked, cart)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (carts.isEmpty()) 1 else carts.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val qty: TextView = itemView.findViewById(R.id.qty)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}