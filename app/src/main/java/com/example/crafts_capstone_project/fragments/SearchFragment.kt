package com.example.crafts_capstone_project.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafts_capstone_project.R
import com.example.crafts_capstone_project.adapter.UserAdapter
import com.example.crafts_capstone_project.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var searchUser: SearchView? = null
    private var recyclerSearchView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerSearchView = view.findViewById(R.id.search_list)
        recyclerSearchView?.setHasFixedSize(true)
        recyclerSearchView?.layoutManager = LinearLayoutManager(context)
        searchUser = view.findViewById(R.id.searchUser)
        mUsers = ArrayList()

        if (FirebaseAuth.getInstance().currentUser != null) {
            retrieveAllUsers()
        } else {
            // Handle the case when the user is not authenticated
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }

        searchUser!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchForUsers(it.lowercase()) }
                return true
            }
        })

        return view
    }
    private fun retrieveAllUsers() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snaps in snapshot.children) {
                    val user = snaps.getValue(Users::class.java)
                        // Exclude the current user's data from appearing in search
                    user?.let {
                        if (it.getUserID() != firebaseUserId) {
                            (mUsers as ArrayList<Users>).add(it)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerSearchView!!.adapter = userAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context!!, "Failed to retrieve users: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchForUsers(input: String) {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.child("users")
            .orderByChild("search").startAt(input).endAt(input + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snaps in snapshot.children) {
                    val user = snaps.getValue(Users::class.java)
                        // Exclude the current user's data from appearing in search
                    if (user!!.getUserID() != firebaseUserId) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = context?.let { UserAdapter(it, mUsers!!, false) }
                recyclerSearchView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}