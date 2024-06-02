package com.example.crafts_capstone_project.models

class Users {
    private var userId: String = ""
    private var username: String = ""
    private var email: String = ""
    private var isOnline: Boolean = false
    private var profile: String = ""
    private var search: String = ""

    constructor()
    constructor(userId: String, username: String, email: String, isOnline: Boolean, profile: String, search: String) {
        this.userId = userId
        this.username = username
        this.email = email
        this.isOnline = isOnline
        this.profile = profile
        this.search = search
    }
    fun getUserID(): String {
        return userId
    }
    fun getUserName(): String {
        return username
    }
    fun getEmail(): String {
        return email
    }
    fun isOnline(): Boolean {
        return isOnline
    }
    fun getProfile(): String {
        return profile
    }
    fun getSearch(): String {
        return search
    }
    fun setUserID(uid: String) {
        this.userId = userId
    }
    fun setUserName(username: String) {
        this.username = username
    }
    fun setEmail(email: String) {
        this.email = email
    }
    fun setIsOline(isLoggedIn: Boolean) {
        this.isOnline = isLoggedIn
    }
    fun setProfile(profile: String) {
        this.profile = profile
    }
    fun setSearch(search: String) {
        this.search = search
    }

}