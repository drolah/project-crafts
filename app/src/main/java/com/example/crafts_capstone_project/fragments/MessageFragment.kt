package com.example.crafts_capstone_project.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.crafts_capstone_project.ChatActivity
import com.example.crafts_capstone_project.NotificationsActivity
import com.example.crafts_capstone_project.OrderActivity
import com.example.crafts_capstone_project.R

class MessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        val chtButton = view.findViewById<ImageButton>(R.id.i_chat)
        chtButton.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        val notifButton = view.findViewById<ImageButton>(R.id.i_notif)
        notifButton.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }

        val orderBtn = view.findViewById<ImageButton>(R.id.i_orders)
        orderBtn.setOnClickListener {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}