package com.example.crafts_capstone_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter (private val messages: List<Message>, private val currentUserEmail: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderEmail == currentUserEmail) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENDER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sender_layout, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.receiver_layout, parent, false)
            ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SenderViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiverViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.sender)
        private val timestamp: TextView = itemView.findViewById(R.id.time)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(message: Message) {
            messageText.text = message.message
            timestamp.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
            checkBox.isChecked = true
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.receiver)
        private val timestamp: TextView = itemView.findViewById(R.id.time)

        fun bind(message: Message) {
            messageText.text = message.message
            timestamp.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
        }
    }
}