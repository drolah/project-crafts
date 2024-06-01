package com.example.crafts_capstone_project

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


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