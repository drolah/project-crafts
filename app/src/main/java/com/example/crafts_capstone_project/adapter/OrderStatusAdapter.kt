package com.example.crafts_capstone_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.data.OrderStatus
import com.example.crafts_capstone_project.R

class OrderStatusAdapter(private var orderStatuses: List<OrderStatus>) : RecyclerView.Adapter<OrderStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_status_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderStatus = orderStatuses[position]
        holder.bind(orderStatus)
    }

    override fun getItemCount(): Int = orderStatuses.size

    fun updateOrderStatuses(newOrderStatuses: List<OrderStatus>) {
        this.orderStatuses = newOrderStatuses
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.order_prod_pay)
        private val orderIdTextView: TextView = itemView.findViewById(R.id.order_prod_ship)
        private val productNameDisplayTextView: TextView = itemView.findViewById(R.id.order_prod_recieve)
        private val returnTextView: TextView = itemView.findViewById(R.id.order_prod_return)

        fun bind(orderStatus: OrderStatus) {
            productNameDisplayTextView.text = orderStatus.productName
            orderIdTextView.text = orderStatus.orderStatusId
            returnTextView.text = "0"
            val totalAmount = if (orderStatus.status == "Paid") "0.0" else orderStatus.totalAmount.toString()
            productNameTextView.text = totalAmount
        }
    }
}
