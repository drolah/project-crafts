package com.example.crafts_capstone_project.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.crafts_capstone_project.MainActivity
import com.example.crafts_capstone_project.MyProducts
import com.example.crafts_capstone_project.OrderStatusActivity
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.SellersOrder
import com.example.crafts_capstone_project.UploadProductActivity
import com.example.crafts_capstone_project.models.Users
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AccountFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var usersReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private val REQUEST_CODE = 100
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private lateinit var profilePic: CircleImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")
        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        val username = view.findViewById<TextView>(R.id.accountName)
        profilePic = view.findViewById(R.id.currentProfileImage)
        val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val refUsers = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.uid)
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val accUsername = user?.getUserName() ?: ""
                    val accProfileImage = user?.getProfile() ?: ""

                    username.text = accUsername
                    if (accProfileImage.isNotEmpty()) {
                        Picasso.get().load(accProfileImage).placeholder(R.drawable.img_20).into(profilePic)
                    } else {
                        Picasso.get().load(R.drawable.img_20).into(profilePic)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
        profilePic.setOnClickListener {
            pickImage()
        }

        val pay = view.findViewById<LinearLayout>(R.id.toPay)
        val ship = view.findViewById<LinearLayout>(R.id.toShip)
        val receive = view.findViewById<LinearLayout>(R.id.toRecieve)
        val returns = view.findViewById<LinearLayout>(R.id.returns)
        val sell = view.findViewById<TextView>(R.id.sell)

        sell.setOnClickListener {
            val intent = Intent(requireContext(), UploadProductActivity::class.java)
            startActivity(intent)
        }

        val settings = view.findViewById<ImageView>(R.id.settings)
        settings.setOnClickListener {
            val options = arrayOf("Orders", "My Products", "Logout")

            val builder = AlertDialog.Builder(requireContext())

            // Set the dialog title
            builder.setTitle("Settings")

            // Set options as buttons
            builder.setItems(options) { dialog, which ->
                // Handle each option click
                when (which) {
                    0 -> {
                        val intent = Intent(requireContext(), SellersOrder::class.java)
                        startActivity(intent)
                    }

                    1 -> {
                        val intent = Intent(requireContext(), MyProducts::class.java)
                        startActivity(intent)
                    }

                    2 -> {
                        val userId = auth.currentUser?.uid
                        userId?.let { uid ->
                            // Set isOnline to false before logging out
                            database.child(uid).child("isOnline").setValue(false)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        // Clear SharedPreferences
                                        val editor = sharedPreferences.edit()
                                        editor.remove("username")
                                        editor.remove("email")
                                        editor.remove("password")
                                        editor.apply()

                                        // Logout and navigate to MainActivity
                                        auth.signOut()
                                        val intent = Intent(requireContext(), MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to update online status", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }
                dialog.dismiss() // Dismiss the dialog after an option is selected
            }

            // Create the AlertDialog
            val dialog: AlertDialog = builder.create()

            // Show the AlertDialog
            dialog.show()
        }

        pay.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatusActivity::class.java)
            startActivity(intent)
        }

        ship.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatusActivity::class.java)
            startActivity(intent)
        }

        receive.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatusActivity::class.java)
            startActivity(intent)
        }

        returns.setOnClickListener {
            val intent = Intent(requireContext(), OrderStatusActivity::class.java)
            startActivity(intent)
        }

        return view
    }
    private fun fetchOrderStatus(action: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("orderStatus")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val orderId = orderSnapshot.key // Get the orderId
                        val productName = orderSnapshot.child("productName").getValue(String::class.java) // Get the productName
                        val totalAmount = orderSnapshot.child("totalAmount").getValue(Double::class.java) // Get the totalAmount

                        // Pass the data to the OrderStatusActivity
                        val intent = Intent(requireContext(), OrderStatusActivity::class.java).apply {
                            putExtra("orderId", orderId)
                            putExtra("productName", productName)
                            putExtra("totalAmount", totalAmount)
                            putExtra("action", action) // Add the action
                        }
                        startActivity(intent)
                        return
                    }
                } else {
                    Toast.makeText(requireContext(), "No order found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch order details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait a moment...")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask<Uri?>(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val mapProfileImg = HashMap<String, Any>()
                    mapProfileImg["profile"] = url
                    usersReference?.updateChildren(mapProfileImg)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            progressBar.dismiss()
                            Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
                            refreshProfilePicture(url)
                        } else {
                            progressBar.dismiss()
                            Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        progressBar.dismiss()
                        Toast.makeText(context, "User reference is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    progressBar.dismiss()
                    Toast.makeText(context, "Upload failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                progressBar.dismiss()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshProfilePicture(url: String) {
        Picasso.get().load(url).placeholder(R.drawable.img_20).into(profilePic)
    }
}