package com.example.crafts_capstone_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class VisitUserProfileActivity : AppCompatActivity() {
    private var userVisitId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_visit_user_profile)

        var userNameDisplay: TextView
        var userProfileImage: CircleImageView
        var userEmailDisplay: TextView

        userNameDisplay = findViewById(R.id.username_display)
        userProfileImage = findViewById(R.id.profile_display)
        userEmailDisplay = findViewById(R.id.email_display)

        userVisitId = intent.getStringExtra("visits_id")
        val ref = FirebaseDatabase.getInstance().getReference("users").child(userVisitId!!)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    val userProfile = user!!.getProfile()
                    userNameDisplay.text = user!!.getUserName()
                    userEmailDisplay.text = user!!.getEmail()

                    if (userProfile.isNotEmpty()) {
                        Picasso.get().load(userProfile).placeholder(R.drawable.img_31).into(userProfileImage)
                    } else {
                        userProfileImage.setImageResource(R.drawable.img_20)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}