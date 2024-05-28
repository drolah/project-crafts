package com.example.crafts_capstone_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoreAdapter(private var userList: MutableList<User>, private val context: Context) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
    private lateinit var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stores_display, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val user = userList[position]
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)

        if (user.username != username && user.email != email) {
            holder.storeName.text = user.username
            holder.itemView.setOnClickListener {
                // Open chat with this user
                val intent = Intent(context, MessengerActivity::class.java)
                intent.putExtra("senderUsername", username)
                intent.putExtra("senderEmail", email)
                intent.putExtra("receiverUsername", user.username)
                intent.putExtra("receiverEmail", user.email)
                context.startActivity(intent)
            }
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