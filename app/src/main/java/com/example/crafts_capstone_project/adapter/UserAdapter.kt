package com.example.crafts_capstone_project.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.MessengerActivity
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.VisitUserProfileActivity
import com.example.crafts_capstone_project.models.Chats
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(mContext: Context, mUserList: List<Users>, isChatCheck: Boolean) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    var lastMessage: String = ""
    init {
        this.mContext = mContext
        this.mUsers = mUserList
        this.isChatCheck = isChatCheck
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTextView: TextView
        init {
            userNameTextView = itemView.findViewById(R.id.usernames)
            profileImageView = itemView.findViewById(R.id.profile_images)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTextView= itemView.findViewById(R.id.message_last)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(
            R.layout.user_search_item_layout,
            viewGroup,
            false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.userNameTextView.text = user.getUserName()
        val profileImageUrl = user.getProfile()
        if (profileImageUrl.isNotEmpty()) {
            Picasso.get().load(profileImageUrl).placeholder(R.drawable.img_31).into(holder.profileImageView)
        } else {
            holder.profileImageView.setImageResource(R.drawable.img_20)
        }
        if (isChatCheck){
            retrieveLastMessage(user.getUserID(), holder.lastMessageTextView)
        } else{
            holder.lastMessageTextView.visibility = View.GONE
        }
        if (isChatCheck){
            if (user.isOnline()){
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }
            else{
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }
        else{
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    val intent = Intent(mContext, MessengerActivity::class.java)
                    intent.putExtra("visits_id", user.getUserID()) // Pass User ID to MessengerActivity
                    mContext.startActivity(intent)
                }
                if (which == 1) {
                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visits_id", user.getUserID()) // Pass User ID to MessengerActivity
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
    }
    private fun retrieveLastMessage(chatUserID: String, lastMessageTextView: TextView) {
        lastMessage = "defaultMessage"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children){
                    val chat: Chats? = snap.getValue(Chats::class.java)
                    if (firebaseUser!=null && chat!=null){
                        if (chat.getReceiver() == firebaseUser!!.uid && chat.getSender() == chatUserID
                            || chat.getReceiver() == chatUserID && chat.getSender() == firebaseUser!!.uid){
                            lastMessage = chat.getMessage()!!
                        }
                    }
                }
                when(lastMessage){
                    "defaultMessage" -> lastMessageTextView.text = "No Message"
                    "Sent a photo." -> lastMessageTextView.text = "Image sent."
                    else -> lastMessageTextView.text = lastMessage
                }
                lastMessage = "defaultMessage"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}