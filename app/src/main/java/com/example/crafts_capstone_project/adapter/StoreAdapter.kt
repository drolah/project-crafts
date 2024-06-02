package com.example.crafts_capstone_project.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.MessengerActivity
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.data.User
import com.example.crafts_capstone_project.models.Users

class StoreAdapter(private var userList: List<Users>, private val context: Context) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stores_display, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val user: Users = userList[position]

            holder.storeName.text = user.getUserName()
            holder.itemView.setOnClickListener {
                // Open chat with this user
                val intent = Intent(context, MessengerActivity::class.java)
                intent.putExtra("visits_id", user.getUserID())
                context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeImage: ImageView = itemView.findViewById(R.id.storeImage)
        val storeName: TextView = itemView.findViewById(R.id.storeName)
    }
}