package com.example.crafts_capstone_project.models

class ChatList {
    private var id: String = ""

    constructor()

    constructor(id: String){
        this.id = id
    }
    fun setId(id: String?){
        this.id = id!!
    }
    fun getId(): String? {
        return id!!
    }
}