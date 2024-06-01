package com.example.crafts_capstone_project

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.adapter.ChatsAdapter
import com.example.crafts_capstone_project.models.Chats
import com.example.crafts_capstone_project.models.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessengerActivity : AppCompatActivity() {
    private var chatsAdapter: ChatsAdapter? = null
    private var mChatList: List<Chats>? = null
    private lateinit var recyclerViewChats: RecyclerView
    var reference: DatabaseReference? = null
    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    private lateinit var mUserProfileImageView: CircleImageView
    private lateinit var mUsernameTextView: TextView
    private lateinit var mMessageInputField: EditText
    private lateinit var mAttachFileButton: ImageButton
    private lateinit var mSendButton: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger)

        intent = intent
        // Retrieve the user ID from the intent
        userIdVisit = intent.getStringExtra("visits_id") ?: "" // Retrieve User ID
        firebaseUser = FirebaseAuth.getInstance().currentUser



        // Initialize RecyclerView for chat messages
        recyclerViewChats = findViewById(R.id.recycler_view_chat_messages)
        recyclerViewChats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChats.layoutManager = linearLayoutManager

        // Initialize views for displaying user's profile and name
        mUserProfileImageView = findViewById(R.id.user_profile_image_messenger)
        mUsernameTextView = findViewById(R.id.username_messenger)
        mMessageInputField = findViewById(R.id.message_input_field)
        mAttachFileButton = findViewById(R.id.attach_file_button_messenger)
        mSendButton = findViewById(R.id.send_message_button)

        // Fetch user data from Firebase
        reference = FirebaseDatabase.getInstance().reference
            .child("users").child(userIdVisit) // Retrieve user data by ID
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    val mUsername = user?.getUserName() ?: ""
                    val mProfileImage = user?.getProfile() ?: ""

                    mUsernameTextView.text = mUsername
                    if (mProfileImage.isNotEmpty()) {
                        Picasso.get().load(mProfileImage).into(mUserProfileImageView)
                    } else {
                        mUserProfileImageView.setImageResource(R.drawable.img_20)
                    }

                    retrieveMessages(firebaseUser!!.uid, userIdVisit, user!!.getProfile())

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessengerActivity", "Failed to retrieve user data: ${error.message}")
            }
        })
        // Back button listener
        val backBtn = findViewById<ImageButton>(R.id.back_button_messenger)
        backBtn.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Send message button listener
        mSendButton.setOnClickListener {
            val message = mMessageInputField.text.toString()
            if (message == "") {
                Toast.makeText(this@MessengerActivity, "Please write a message ...", Toast.LENGTH_SHORT).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            mMessageInputField.setText("")
        }
        // Attach files button listener
        mAttachFileButton.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
        }
        seenMessage(userIdVisit)
    }

    // Retrieve messages between the current user and the selected user
    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chats>).clear()
                for (snapshot in p0.children) {
                    val chat: Chats? = snapshot.getValue(Chats::class.java)
                    if (chat!!.getReceiver() == senderId && chat.getSender() == receiverId ||
                        chat.getReceiver() == receiverId && chat.getSender() == senderId) {
                        (mChatList as ArrayList<Chats>).add(chat)
                    }
                }
                chatsAdapter = ChatsAdapter(this@MessengerActivity, (mChatList as ArrayList<Chats>), receiverImageUrl)
                recyclerViewChats.adapter = chatsAdapter
                recyclerViewChats.scrollToPosition((mChatList as ArrayList<Chats>).size - 1) // Scroll to the last message
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessengerActivity", "Failed to retrieve messages: ${error.message}")
            }
        })
    }
    // Send message to the selected user
    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String){
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val chatsListReference = FirebaseDatabase.getInstance().reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()){
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance().reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("MessengerActivity", "Failed to add user to chat list: ${error.message}")
                        }
                    })
                }
            }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait a moment...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser?.uid
                    messageHashMap["message"] = "Sent a photo."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId
                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { chatTask ->
                            if (chatTask.isSuccessful) {
                                progressBar.dismiss()
                            } else {
                                progressBar.dismiss()
                                Toast.makeText(
                                    this@MessengerActivity,
                                    "Failed to send image: ${chatTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    progressBar.dismiss()
                    Toast.makeText(
                        this@MessengerActivity,
                        "Failed to upload image: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun sendMessage(userId: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chat")
         reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chats::class.java)
                    if (chat!!.getReceiver() == firebaseUser!!.uid && chat.getSender() == userId) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chats::class.java)
                    if (chat!!.getReceiver() == firebaseUser!!.uid && chat.getSender() == userId) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}