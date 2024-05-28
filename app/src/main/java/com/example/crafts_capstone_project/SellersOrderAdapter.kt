package com.example.crafts_capstone_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SellersOrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ORDER = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ORDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ORDER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.seller_layout, parent, false)
            OrderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.empty_order_layout, parent, false)
            EmptyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderViewHolder) {
            val order = orders[position]
            with(holder) {
                name.text = order.name
                price.text = "P ${order.price}"
                quantity.text = order.quantity.toString()
                total.text = order.total.toString()
                customerName.text = order.username

                ImageDownloaderTask(image).execute(order.image)
            }
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
        val image: ImageView = itemView.findViewById(R.id.image)
        val customerName: TextView = itemView.findViewById(R.id.customerName)
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}