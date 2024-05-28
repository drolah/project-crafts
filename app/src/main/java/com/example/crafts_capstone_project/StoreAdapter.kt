package com.example.crafts_capstone_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private var userList: MutableList<User>) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stores_display, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val user = userList[position]
        holder.storeName.text = user.username
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(newUsers: List<User>) {
        userList.clear()
        userList.addAll(newUsers)
        notifyDataSetChanged()
    }

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage: ImageView = itemView.findViewById(R.id.storeImage)
        val storeName: TextView = itemView.findViewById(R.id.storeName)
    }
}