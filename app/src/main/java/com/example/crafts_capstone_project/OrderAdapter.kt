package com.example.crafts_capstone_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ORDER = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ORDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ORDER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_layout, parent, false)
            OrderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_order_layout, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderViewHolder) {
            val order = orders[position]
            holder.name.text = order.name
            holder.price.text = "P ${order.price}"
            holder.quantity.text = order.quantity
            holder.total.text = order.total
            holder.image.setImageResource(order.imageResource) // Set the image resource
        }
    }

    override fun getItemCount(): Int {
        return if (orders.isEmpty()) 1 else orders.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val quantity: TextView = itemView.findViewById(R.id.qty)
        val total: TextView = itemView.findViewById(R.id.total)
        val image: ImageView = itemView.findViewById(R.id.image) // ImageView for the product image
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}