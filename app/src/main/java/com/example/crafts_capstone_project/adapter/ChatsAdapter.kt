package com.example.crafts_capstone_project.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.ViewFullImageActivity
import com.example.crafts_capstone_project.models.Chats
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(
    mContext: Context,
    mChatsList: List<Chats>,
    imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private val mContext: Context
    private val mChatsList: List<Chats>
    private val imageUrl: String
    private var firebaseUser: FirebaseUser? = null
    private val onUserClickListener: ((Users) -> Unit)? = null

    init {
        this.mContext = mContext
        this.mChatsList = mChatsList
        this.imageUrl = imageUrl
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == 1) {
            LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mChatsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chats = mChatsList[position]

        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(holder.imageProfile)
        } else {
            holder.imageProfile?.setImageResource(R.drawable.img_20)
        }

        if (chat.getMessage() == "Sent a photo." && chat.getUrl().isNotEmpty()) {
            val isSender = chat.getSender() == (firebaseUser?.uid)
            holder.showTextMessage?.visibility = View.GONE
            holder.leftImageView?.visibility = if (!isSender) View.VISIBLE else View.GONE
            holder.rightImageView?.visibility = if (isSender) View.VISIBLE else View.GONE

            if (isSender){
                holder.rightImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Image",
                        "Delete",
                        "Cancel"
                    )
                    var builder = android.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0) {
                            //View Image
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        } else if (which == 1) {
                            //Delete
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
            else if (!isSender) {
                holder.leftImageView!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Image",
                        "Cancel"
                    )
                    var builder = android.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0) {
                            //View Image
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }
            }
            else {
                holder.leftImageView?.visibility = View.GONE
                holder.rightImageView?.visibility = View.GONE
            }

            val targetImageView = if (isSender) holder.rightImageView else holder.leftImageView
            if (chat.getUrl().isNotEmpty()) {
                Picasso.get().load(chat.getUrl()).into(targetImageView)
            } else {
                targetImageView?.setImageResource(R.drawable.img_20)
            }


        } else {
            holder.showTextMessage?.text = chat.getMessage()
            if (firebaseUser!!.uid == chat.getSender()) {
                holder.showTextMessage!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message ",
                        "Cancel"
                    )
                    var builder = android.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want?")
                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0) {
                            //Delete
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
        }

        if (position == mChatsList.size - 1) {
            if (chat.isIsSeen()) {
                holder.textSeen?.text = if (chat.isIsSeen()) "Seen" else "Sent"
                if (chat.getMessage() == "sent you an image." && !chat.getUrl().equals("")) {
                    val lp: RelativeLayout.LayoutParams? = holder.textSeen?.layoutParams as? RelativeLayout.LayoutParams?
                    lp?.setMargins(0, 245, 10, 0)
                    holder.textSeen?.layoutParams = lp
                }
            }
        } else {
            holder.textSeen?.visibility = View.GONE
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageProfile: CircleImageView? = view.findViewById(R.id.show_image_profile)
        var showTextMessage: TextView? = view.findViewById(R.id.show_text_message)
        var leftImageView: ImageView? = view.findViewById(R.id.left_image_message)
        var textSeen: TextView? = view.findViewById(R.id.text_seen)
        var rightImageView: ImageView? = view.findViewById(R.id.right_image_message)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val chat = mChatsList[position]
                    val user = Users(chat.getReceiver(), "", "", true, "", "", "") // Create a User object, adjust parameters as needed
                    onUserClickListener?.invoke(user)
                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (mChatsList[position].getSender() == firebaseUser?.uid) 1 else 0
    }
    private fun deleteSentMessage(position: Int, holder: ChatsAdapter.ViewHolder){
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatsList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Deleted.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(holder.itemView.context, "Failed to delete.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
