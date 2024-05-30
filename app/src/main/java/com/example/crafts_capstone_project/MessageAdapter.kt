package com.example.crafts_capstone_project

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date


class MessageAdapter(private val messages: List<Message>, private val userEmail: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_RECEIVER = 1
        const val TYPE_SENDER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RECEIVER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.receiver_layout, parent, false)
            ReceiverViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.sender_layout, parent, false)
            SenderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.itemViewType == TYPE_RECEIVER) {
            val receiverViewHolder = holder as ReceiverViewHolder
            receiverViewHolder.receiverMessage.text = message.message
        } else {
            val senderViewHolder = holder as SenderViewHolder
            senderViewHolder.senderMessage.text = message.message
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderEmail == userEmail) TYPE_SENDER else TYPE_RECEIVER
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage: TextView = itemView.findViewById(R.id.receiver)
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessage: TextView = itemView.findViewById(R.id.sender)
    }
}